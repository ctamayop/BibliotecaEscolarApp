package cl.biblioteca.view;

import cl.biblioteca.controller.BibliotecaController;
import cl.biblioteca.controller.LoginController;
import cl.biblioteca.dao.impl.PrestamoDaoImpl;
import cl.biblioteca.model.Categoria;
import cl.biblioteca.model.Estudiante;
import cl.biblioteca.model.Libro;
import cl.biblioteca.model.Prestamo;
import cl.biblioteca.model.Usuario;
import cl.biblioteca.service.PrestamoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BibliotecaFrame extends JFrame {

    private final BibliotecaController controller = new BibliotecaController();
    private final LoginController loginController = new LoginController();
    private final PrestamoService prestamoService = new PrestamoService();
    private final PrestamoDaoImpl prestamoDao = new PrestamoDaoImpl();

    private Usuario usuarioActual;
    private JPanel contenido;
    private JPanel menu;

    public BibliotecaFrame() {
        setTitle("Biblioteca Escolar - Sistema de Gestión");
        setSize(1100, 680);
        setMinimumSize(new Dimension(880, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mostrarLogin();
    }

    private boolean esBibliotecario() {
        return usuarioActual != null && "bibliotecario".equals(usuarioActual.getRol());
    }

    private void mostrarLogin() {
        usuarioActual = null;
        getContentPane().removeAll();
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        JLabel titulo = new JLabel("Biblioteca Escolar", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 25));
        JTextField correo = new JTextField(23);
        JPasswordField clave = new JPasswordField(23);
        JButton ingresar = new JButton("Iniciar sesión");
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; panel.add(titulo, c);
        c.gridwidth = 1; c.gridy = 1; c.gridx = 0; panel.add(new JLabel("Correo:"), c);
        c.gridx = 1; panel.add(correo, c);
        c.gridx = 0; c.gridy = 2; panel.add(new JLabel("Contraseña:"), c);
        c.gridx = 1; panel.add(clave, c);
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2; panel.add(ingresar, c);
        ingresar.addActionListener(e -> {
            String email = correo.getText().trim();
            char[] caracteres = clave.getPassword();
            String contrasena = new String(caracteres);
            Arrays.fill(caracteres, '\0');
            if (email.isEmpty() || contrasena.isEmpty()) {
                mensaje("Ingrese su correo y contraseña.");
                return;
            }
            ingresar.setEnabled(false);
            new SwingWorker<Usuario, Void>() {
                @Override protected Usuario doInBackground() throws Exception {
                    return loginController.iniciarSesion(email, contrasena);
                }
                @Override protected void done() {
                    ingresar.setEnabled(true);
                    try {
                        Usuario encontrado = get();
                        if (encontrado == null) {
                            mensaje("Correo o contraseña incorrectos.");
                            clave.setText("");
                        } else {
                            usuarioActual = encontrado;
                            mostrarSistema();
                        }
                    } catch (Exception ex) { mostrarError(ex); }
                }
            }.execute();
        });
        getRootPane().setDefaultButton(ingresar);
        add(panel);
        revalidate(); repaint();
    }

    private void mostrarSistema() {
        getContentPane().removeAll();
        JPanel principal = new JPanel(new BorderLayout());
        contenido = new JPanel(new BorderLayout());
        menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setPreferredSize(new Dimension(210, 0));
        menu.setBorder(BorderFactory.createEmptyBorder(18, 10, 15, 10));
        JLabel titulo = new JLabel("BIBLIOTECA ESCOLAR");
        titulo.setFont(new Font("Arial", Font.BOLD, 15));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        menu.add(titulo);
        menu.add(Box.createVerticalStrut(25));
        botonMenu("Inicio / Mi perfil", this::mostrarInicio);
        botonMenu("Libros", () -> mostrarTabla("Libros"));
        botonMenu("Préstamos", this::mostrarPrestamos);
        if (esBibliotecario()) {
            botonMenu("Categorías", () -> mostrarTabla("Categorías"));
            botonMenu("Estudiantes", () -> mostrarTabla("Estudiantes"));
            botonMenu("Usuarios", () -> mostrarTabla("Usuarios"));
            botonMenu("Reportes", this::mostrarReportes);
        }
        menu.add(Box.createVerticalGlue());
        botonMenu("Cerrar sesión", this::mostrarLogin);
        principal.add(menu, BorderLayout.WEST);
        principal.add(contenido, BorderLayout.CENTER);
        add(principal);
        mostrarInicio();
        revalidate(); repaint();
    }

    private void botonMenu(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.setMaximumSize(new Dimension(190, 40));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.addActionListener(e -> accion.run());
        menu.add(boton);
        menu.add(Box.createVerticalStrut(9));
    }

    private JPanel prepararPanel(String titulo) {
        contenido.removeAll();
        JPanel panel = new JPanel(new BorderLayout(10, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(new Font("Arial", Font.BOLD, 23));
        panel.add(etiqueta, BorderLayout.NORTH);
        contenido.add(panel, BorderLayout.CENTER);
        contenido.revalidate(); contenido.repaint();
        return panel;
    }

    private void mostrarInicio() {
        JPanel panel = prepararPanel("Mi perfil");
        JTextArea perfil = new JTextArea();
        perfil.setEditable(false);
        perfil.setBackground(panel.getBackground());
        perfil.setFont(new Font("Arial", Font.PLAIN, 17));
        perfil.setText("Bienvenido/a a Biblioteca Escolar\n\n" +
                "ID: " + usuarioActual.getId() + "\n\n" +
                "Nombre: " + usuarioActual.getNombre() + "\n\n" +
                "RUT: " + usuarioActual.getRut() + "\n\n" +
                "Correo: " + usuarioActual.getCorreo() + "\n\n" +
                "Rol: " + usuarioActual.getRol());
        panel.add(perfil, BorderLayout.CENTER);
    }

    private JTable crearTabla(String[] columnas, List<Object[]> filas) {
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        };
        for (Object[] fila : filas) modelo.addRow(fila);
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(27);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int col = 0; col < columnas.length; col++) {
            int ancho = 120;
            if (columnas[col].equals("ID") || columnas[col].equals("Stock")) ancho = 65;
            if (columnas[col].equals("Título") || columnas[col].equals("Nombre") ||
                    columnas[col].equals("Correo")) ancho = 220;
            if (columnas[col].equals("Autor") || columnas[col].equals("Editorial")) ancho = 170;
            if (columnas[col].equals("ISBN")) ancho = 150;
            tabla.getColumnModel().getColumn(col).setPreferredWidth(ancho);
        }
        return tabla;
    }

    private int idSeleccionado(JTable tabla) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { mensaje("Seleccione un registro."); return -1; }
        int filaModelo = tabla.convertRowIndexToModel(fila);
        return (int) tabla.getModel().getValueAt(filaModelo, 0);
    }

    private void mostrarTabla(String tipo) {
        if (!esBibliotecario() && !tipo.equals("Libros")) {
            mensaje("No tiene permisos para acceder."); return;
        }
        try {
            String[] columnas;
            List<Object[]> filas = new ArrayList<>();
            switch (tipo) {
                case "Libros":
                    columnas = new String[]{"ID", "Título", "Autor", "ISBN", "Editorial", "Stock", "Categoría"};
                    for (Libro l : controller.listarLibros()) {
                        filas.add(new Object[]{l.getId(), l.getTitulo(), l.getAutor(), l.getIsbn(),
                                l.getEditorial(), l.getStock(), nombreCategoria(l.getIdCategoria())});
                    }
                    break;
                case "Categorías":
                    columnas = new String[]{"ID", "Nombre"};
                    for (Categoria cat : controller.listarCategorias())
                        filas.add(new Object[]{cat.getId(), cat.getNombre()});
                    break;
                case "Estudiantes":
                    columnas = new String[]{"ID", "Nombre", "RUT", "Curso", "Correo"};
                    for (Estudiante est : controller.listarEstudiantes())
                        filas.add(new Object[]{est.getId(), est.getNombre(), est.getRut(),
                                est.getCurso(), est.getCorreo()});
                    break;
                case "Usuarios":
                    columnas = new String[]{"ID", "Nombre", "RUT", "Correo", "Rol"};
                    for (Usuario u : controller.listarUsuarios())
                        filas.add(new Object[]{u.getId(), u.getNombre(), u.getRut(),
                                u.getCorreo(), u.getRol()});
                    break;
                default: return;
            }
            JPanel panel = prepararPanel("Gestión de " + tipo);
            JTable tabla = crearTabla(columnas, filas);
            panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
            JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton refrescar = new JButton("Refrescar");
            refrescar.addActionListener(e -> mostrarTabla(tipo));
            acciones.add(refrescar);
            if (esBibliotecario()) {
                JButton crear = new JButton("Agregar");
                JButton modificar = new JButton("Modificar");
                JButton eliminar = new JButton("Eliminar");
                crear.addActionListener(e -> formulario(tipo, 0));
                modificar.addActionListener(e -> {
                    int id = idSeleccionado(tabla);
                    if (id > 0) formulario(tipo, id);
                });
                eliminar.addActionListener(e -> {
                    int id = idSeleccionado(tabla);
                    if (id > 0) eliminarRegistro(tipo, id);
                });
                acciones.add(crear); acciones.add(modificar); acciones.add(eliminar);
            }
            panel.add(acciones, BorderLayout.SOUTH);
        } catch (SQLException ex) { mostrarError(ex); }
    }

    private String nombreCategoria(int id) throws SQLException {
        for (Categoria c : controller.listarCategorias()) {
            if (c.getId() == id) return c.getNombre();
        }
        return "Sin categoría";
    }

    private void formulario(String tipo, int id) {
        if (!esBibliotecario()) { mensaje("Acceso no autorizado."); return; }
        try {
            JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
            List<JTextField> campos = new ArrayList<>();
            JComboBox<String> combo = null;
            List<Categoria> categorias = null;
            Object anterior = null;
            String[] etiquetas;
            String[] datosIniciales = null;

            switch (tipo) {
                case "Libros":
                    etiquetas = new String[]{"Título", "Autor", "ISBN", "Editorial", "Stock"};
                    for (Libro l : controller.listarLibros()) if (l.getId() == id) {
                        anterior = l;
                        datosIniciales = new String[]{l.getTitulo(), l.getAutor(), l.getIsbn(),
                                l.getEditorial(), String.valueOf(l.getStock())};
                    }
                    categorias = controller.listarCategorias();
                    break;
                case "Categorías":
                    etiquetas = new String[]{"Nombre"};
                    for (Categoria c : controller.listarCategorias()) if (c.getId() == id) {
                        anterior = c; datosIniciales = new String[]{c.getNombre()};
                    }
                    break;
                case "Estudiantes":
                    etiquetas = new String[]{"Nombre", "RUT", "Curso", "Correo"};
                    for (Estudiante e : controller.listarEstudiantes()) if (e.getId() == id) {
                        anterior = e;
                        datosIniciales = new String[]{e.getNombre(), e.getRut(), e.getCurso(), e.getCorreo()};
                    }
                    break;
                case "Usuarios":
                    etiquetas = new String[]{"Nombre", "RUT", "Correo", "Contraseña"};
                    for (Usuario u : controller.listarUsuarios()) if (u.getId() == id) {
                        anterior = u;
                        datosIniciales = new String[]{u.getNombre(), u.getRut(), u.getCorreo(), ""};
                    }
                    break;
                default: return;
            }
            if (id > 0 && anterior == null) { mensaje("El registro ya no existe."); return; }
            for (int i = 0; i < etiquetas.length; i++) {
                panel.add(new JLabel(etiquetas[i] + ":"));
                JTextField campo = etiquetas[i].equals("Contraseña")
                        ? new JPasswordField(22) : new JTextField(22);
                if (datosIniciales != null) campo.setText(datosIniciales[i]);
                campos.add(campo);
                panel.add(campo);
            }
            if (tipo.equals("Libros")) {
                if (categorias.isEmpty()) { mensaje("Debe registrar una categoría antes."); return; }
                panel.add(new JLabel("Categoría:"));
                combo = new JComboBox<>();
                int seleccionado = 0;
                for (int i = 0; i < categorias.size(); i++) {
                    Categoria c = categorias.get(i);
                    combo.addItem(c.getNombre());
                    if (anterior != null && c.getId() == ((Libro) anterior).getIdCategoria()) seleccionado = i;
                }
                combo.setSelectedIndex(seleccionado);
                panel.add(combo);
            }
            if (tipo.equals("Usuarios")) {
                panel.add(new JLabel("Rol:"));
                combo = new JComboBox<>(new String[]{"bibliotecario", "estudiante"});
                if (anterior != null) combo.setSelectedItem(((Usuario) anterior).getRol());
                panel.add(combo);
            }
            String titulo = (id == 0 ? "Agregar " : "Modificar ") + tipo;
            if (JOptionPane.showConfirmDialog(this, panel, titulo,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
            String[] datos = new String[campos.size()];
            for (int i = 0; i < campos.size(); i++) datos[i] = campos.get(i).getText().trim();
            boolean ok = false;
            switch (tipo) {
                case "Libros":
                    int idCat = categorias.get(combo.getSelectedIndex()).getId();
                    Libro l = new Libro(id, datos[0], datos[1], datos[2], datos[3],
                            Integer.parseInt(datos[4]), idCat);
                    if (id == 0) { controller.crearLibro(l); ok = true; }
                    else ok = controller.actualizarLibro(l);
                    break;
                case "Categorías":
                    Categoria cat = new Categoria(id, datos[0]);
                    if (id == 0) { controller.crearCategoria(cat); ok = true; }
                    else ok = controller.actualizarCategoria(cat);
                    break;
                case "Estudiantes":
                    Estudiante est = new Estudiante(id, datos[0], datos[1], datos[2], datos[3]);
                    if (id == 0) { controller.crearEstudiante(est); ok = true; }
                    else ok = controller.actualizarEstudiante(est);
                    break;
                case "Usuarios":
                    String clave = datos[3];
                    if (id > 0 && clave.isEmpty()) clave = ((Usuario) anterior).getContrasena();
                    Usuario u = new Usuario(id, datos[0], datos[1], datos[2], clave,
                            (String) combo.getSelectedItem());
                    if (id == 0) { controller.crearUsuario(u); ok = true; }
                    else ok = controller.actualizarUsuario(u);
                    break;
            }
            mensaje(ok ? "Operación realizada correctamente." : "No se modificó ningún registro.");
            mostrarTabla(tipo);
        } catch (NumberFormatException ex) {
            mensaje("Ingrese un número válido para el stock.");
        } catch (SQLException ex) { mostrarError(ex); }
    }

    private void eliminarRegistro(String tipo, int id) {
        if (!esBibliotecario()) { mensaje("Acceso no autorizado."); return; }
        if (tipo.equals("Usuarios") && usuarioActual.getId() == id) {
            mensaje("No puede eliminar la cuenta con la que inició sesión."); return;
        }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar el registro seleccionado?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        try {
            boolean ok;
            switch (tipo) {
                case "Libros": ok = controller.eliminarLibro(id); break;
                case "Categorías": ok = controller.eliminarCategoria(id); break;
                case "Estudiantes": ok = controller.eliminarEstudiante(id); break;
                case "Usuarios": ok = controller.eliminarUsuario(id); break;
                default: return;
            }
            mensaje(ok ? "Registro eliminado." : "El registro no existe.");
            mostrarTabla(tipo);
        } catch (SQLException ex) { mostrarError(ex); }
    }

    private int estudianteActualId() throws SQLException {
        for (Estudiante e : controller.listarEstudiantes()) {
            if (e.getRut().equals(usuarioActual.getRut())) return e.getId();
        }
        return -1;
    }

    private String nombreEstudiante(int id, List<Estudiante> estudiantes) {
        for (Estudiante e : estudiantes) if (e.getId() == id) return e.getNombre();
        return "Estudiante #" + id;
    }

    private String tituloLibro(int id, List<Libro> libros) {
        for (Libro l : libros) if (l.getId() == id) return l.getTitulo();
        return "Libro #" + id;
    }

    private void mostrarPrestamos() {
        try {
            List<Prestamo> prestamos = prestamoDao.listar();
            List<Estudiante> estudiantes = controller.listarEstudiantes();
            List<Libro> libros = controller.listarLibros();
            int estudianteId = esBibliotecario() ? -1 : estudianteActualId();
            List<Object[]> filas = new ArrayList<>();
            for (Prestamo p : prestamos) {
                if (!esBibliotecario() && p.getIdEstudiante() != estudianteId) continue;
                filas.add(new Object[]{p.getId(), nombreEstudiante(p.getIdEstudiante(), estudiantes),
                        tituloLibro(p.getIdLibro(), libros), p.getFechaPrestamo(),
                        p.getFechaDevolucion(), p.isDevuelto() ? "Sí" : "No",
                        prestamoService.estaAtrasado(p) ? "Sí" : "No"});
            }
            JPanel panel = prepararPanel("Préstamos y devoluciones");
            JTable tabla = crearTabla(new String[]{"ID", "Estudiante", "Libro", "Fecha préstamo",
                    "Vencimiento", "Devuelto", "Atrasado"}, filas);
            tabla.getColumnModel().getColumn(1).setPreferredWidth(160);
            tabla.getColumnModel().getColumn(2).setPreferredWidth(230);
            panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
            JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton solicitar = new JButton("Solicitar préstamo");
            JButton devolver = new JButton("Registrar devolución");
            JButton refrescar = new JButton("Refrescar");
            solicitar.addActionListener(e -> registrarPrestamo());
            devolver.addActionListener(e -> {
                int id = idSeleccionado(tabla);
                if (id > 0) registrarDevolucion(id);
            });
            refrescar.addActionListener(e -> mostrarPrestamos());
            botones.add(solicitar); botones.add(devolver); botones.add(refrescar);
            panel.add(botones, BorderLayout.SOUTH);
        } catch (SQLException ex) { mostrarError(ex); }
    }

    private void registrarPrestamo() {
        try {
            int idEstudiante;
            if (esBibliotecario()) {
                List<Estudiante> estudiantes = controller.listarEstudiantes();
                if (estudiantes.isEmpty()) { mensaje("No hay estudiantes registrados."); return; }
                String[] opciones = new String[estudiantes.size()];
                for (int i = 0; i < estudiantes.size(); i++)
                    opciones[i] = estudiantes.get(i).getId() + " - " + estudiantes.get(i).getNombre();
                String elegido = (String) JOptionPane.showInputDialog(this, "Seleccione un estudiante:",
                        "Nuevo préstamo", JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
                if (elegido == null) return;
                idEstudiante = Integer.parseInt(elegido.split(" - ")[0]);
            } else {
                idEstudiante = estudianteActualId();
                if (idEstudiante <= 0) { mensaje("No se encontró un estudiante asociado a su RUT."); return; }
            }
            List<Libro> disponibles = new ArrayList<>();
            for (Libro libro : controller.listarLibros()) if (libro.getStock() > 0) disponibles.add(libro);
            if (disponibles.isEmpty()) { mensaje("No hay libros disponibles."); return; }
            String[] opciones = new String[disponibles.size()];
            for (int i = 0; i < disponibles.size(); i++) {
                Libro l = disponibles.get(i);
                opciones[i] = l.getId() + " - " + l.getTitulo() + " (Stock: " + l.getStock() + ")";
            }
            String elegido = (String) JOptionPane.showInputDialog(this, "Seleccione un libro:",
                    "Nuevo préstamo", JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
            if (elegido == null) return;
            int idLibro = Integer.parseInt(elegido.split(" - ")[0]);
            int estudiante = idEstudiante;
            new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() throws Exception {
                    prestamoService.registrarPrestamo(estudiante, idLibro);
                    return null;
                }
                @Override protected void done() {
                    try { get(); mensaje("Préstamo registrado correctamente."); }
                    catch (Exception ex) { mostrarError(ex); }
                    mostrarPrestamos();
                }
            }.execute();
        } catch (SQLException ex) { mostrarError(ex); }
    }

    private void registrarDevolucion(int idPrestamo) {
        if (JOptionPane.showConfirmDialog(this, "¿Confirmar devolución del préstamo #" + idPrestamo + "?",
                "Devolución", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() throws Exception {
                if (!esBibliotecario()) {
                    int miId = estudianteActualId();
                    boolean propio = false;
                    for (Prestamo p : prestamoDao.listar()) {
                        if (p.getId() == idPrestamo && p.getIdEstudiante() == miId) {
                            propio = true; break;
                        }
                    }
                    if (!propio) throw new SQLException("Préstamo no autorizado.");
                }
                return prestamoService.registrarDevolucion(idPrestamo);
            }
            @Override protected void done() {
                try {
                    mensaje(get() ? "Devolución registrada correctamente." :
                            "El préstamo ya fue devuelto o no existe.");
                } catch (Exception ex) { mostrarError(ex); }
                mostrarPrestamos();
            }
        }.execute();
    }

    private void mostrarReportes() {
        if (!esBibliotecario()) { mensaje("Acceso no autorizado."); return; }
        try {
            List<Prestamo> prestamos = prestamoDao.listar();
            List<Libro> libros = controller.listarLibros();
            List<Estudiante> estudiantes = controller.listarEstudiantes();
            JPanel panel = prepararPanel("Reportes de biblioteca");
            JTabbedPane pestanas = new JTabbedPane();

            List<Libro> orden = new ArrayList<>(libros);
            orden.sort(Comparator.comparingInt((Libro l) -> contarPrestamos(prestamos, l.getId())).reversed());
            List<Object[]> ranking = new ArrayList<>();
            for (Libro l : orden) ranking.add(new Object[]{l.getTitulo(), contarPrestamos(prestamos, l.getId())});
            pestanas.addTab("Libros más prestados", new JScrollPane(
                    crearTabla(new String[]{"Título", "Cantidad de préstamos"}, ranking)));

            List<Object[]> activos = new ArrayList<>();
            for (Prestamo p : prestamos) if (!p.isDevuelto()) {
                activos.add(new Object[]{p.getId(), tituloLibro(p.getIdLibro(), libros),
                        nombreEstudiante(p.getIdEstudiante(), estudiantes), p.getFechaDevolucion(),
                        prestamoService.estaAtrasado(p) ? "Sí" : "No"});
            }
            pestanas.addTab("Préstamos activos", new JScrollPane(
                    crearTabla(new String[]{"ID", "Libro", "Estudiante", "Vencimiento", "Atrasado"}, activos)));

            List<Object[]> historial = new ArrayList<>();
            for (Prestamo p : prestamos) historial.add(new Object[]{p.getId(),
                    nombreEstudiante(p.getIdEstudiante(), estudiantes), tituloLibro(p.getIdLibro(), libros),
                    p.getFechaPrestamo(), p.isDevuelto() ? "Devuelto" : "Pendiente"});
            pestanas.addTab("Historial por estudiante", new JScrollPane(
                    crearTabla(new String[]{"ID", "Estudiante", "Libro", "Fecha", "Estado"}, historial)));
            panel.add(pestanas, BorderLayout.CENTER);
        } catch (SQLException ex) { mostrarError(ex); }
    }

    private int contarPrestamos(List<Prestamo> prestamos, int libroId) {
        int contador = 0;
        for (Prestamo p : prestamos) if (p.getIdLibro() == libroId) contador++;
        return contador;
    }

    private void mensaje(String texto) { JOptionPane.showMessageDialog(this, texto); }

    private void mostrarError(Exception ex) {
        Throwable causa = ex;
        if (ex instanceof ExecutionException && ex.getCause() != null) causa = ex.getCause();
        JOptionPane.showMessageDialog(this, "Error: " + causa.getMessage(),
                "Error del sistema", JOptionPane.ERROR_MESSAGE);
    }
}
