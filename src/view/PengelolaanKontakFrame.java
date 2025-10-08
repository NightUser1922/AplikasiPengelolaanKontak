/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;
import controller.KontakController;
import java.io.*;
import model.Kontak;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Acer
 */
public class PengelolaanKontakFrame extends javax.swing.JFrame {
    private DefaultTableModel model;
    private KontakController controller;
    
    /**
     * Creates new form PengelolaanKontakFrame
     */
    public PengelolaanKontakFrame() {
        initComponents();
        controller = new KontakController();
       model = new DefaultTableModel(new String[]
        {"ID", "Nama", "Nomor Telepon", "Kategori"}, 0);
        tblKontak.setModel(model);
        
        loadContacts();
    }
     private void loadContacts() {
    try {
        model.setRowCount(0);
        List<Kontak> contacts = controller.getAllContacts();
        for (Kontak contact : contacts) {
            model.addRow(new Object[]{
                contact.getId(),            // ID dari DB
                contact.getNama(),
                contact.getNomorTelepon(),
                contact.getKategori()
            });
        }
    } catch (SQLException e) {
        showError(e.getMessage());
    }
}

        private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error",
        JOptionPane.ERROR_MESSAGE);
        }
        private void addContact() {
        String nama = txtNama.getText().trim();
        String nomorTelepon = txtNomorTelepon.getText().trim();
        String kategori = (String) cmbKategori.getSelectedItem();
        if (!validatePhoneNumber(nomorTelepon)) {
        return; // Validasi nomor telepon gagal
        }
        try {
        if (controller.isDuplicatePhoneNumber(nomorTelepon, null)) {
        JOptionPane.showMessageDialog(this, "Kontak nomor telepon ini sudah ada.", "Kesalahan", JOptionPane.WARNING_MESSAGE);

        return;
        }
        controller.addContact(nama, nomorTelepon, kategori);
        loadContacts();
        JOptionPane.showMessageDialog(this, "Kontak berhasilditambahkan!");
        clearInputFields();
        } catch (SQLException ex) {
        showError("Gagal menambahkan kontak: " + ex.getMessage());
        }
        }
        private boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nomor telepon tidak bolehkosong.");
        return false;
        }
        if (!phoneNumber.matches("\\d+")) { // Hanya angka
        JOptionPane.showMessageDialog(this, "Nomor telepon hanya boleh berisi angka.");
        return false;
        }
        if (phoneNumber.length() < 8 || phoneNumber.length() > 15) { // Panjang 8-15
        JOptionPane.showMessageDialog(this, "Nomor telepon harus memiliki panjang antara 8 hingga 15 karakter.");
        return false;
        }
        return true;
        }
        private void clearInputFields() {
             txtNama.setText("");
        txtNomorTelepon.setText("");
        cmbKategori.setSelectedIndex(0);
        }      
        
private void editContact() {
    int selectedRow = tblKontak.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin diperbarui.", "Kesalahan", JOptionPane.WARNING_MESSAGE);
        return;
    }
    // Ambil ID dari model (bukan view)
    Object idObj = model.getValueAt(selectedRow, 0);
    int id;
    if (idObj instanceof Number) {
        id = ((Number) idObj).intValue();
    } else {
        try {
            id = Integer.parseInt(idObj.toString());
        } catch (NumberFormatException e) {
            showError("ID kontak tidak valid.");
            return;
        }
    }

String nama = txtNama.getText().trim();
String nomorTelepon = txtNomorTelepon.getText().trim();
String kategori = (String) cmbKategori.getSelectedItem();

if (nama.isEmpty()) {
    JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong.");
    return;
}

if (!validatePhoneNumber(nomorTelepon)) {
    return;
}

    if (!validatePhoneNumber(nomorTelepon)) {
        return;
    }
    try {
        if (controller.isDuplicatePhoneNumber(nomorTelepon, id)) {
            JOptionPane.showMessageDialog(this, "Kontak nomor telepon ini sudah ada.", "Kesalahan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        controller.updateContact(id, nama, nomorTelepon, kategori);
        loadContacts();
        JOptionPane.showMessageDialog(this, "Kontak berhasil diperbarui!");
        clearInputFields();
    } catch (SQLException ex) {
        showError("Gagal memperbarui kontak: " + ex.getMessage());
    }
}

    private void populateInputFields(int selectedRow) {
       String nama = model.getValueAt(selectedRow, 1).toString();
       String nomorTelepon = model.getValueAt(selectedRow, 2).toString();
       String kategori = model.getValueAt(selectedRow, 3).toString();
       txtNama.setText(nama);
       txtNomorTelepon.setText(nomorTelepon);
       cmbKategori.setSelectedItem(kategori);
   }

 private void deleteContact() {
    int selectedRow = tblKontak.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin dihapus.");
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus kontak ini?",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        DefaultTableModel model = (DefaultTableModel) tblKontak.getModel();
        Object idObj = model.getValueAt(selectedRow, 0);
        int id = Integer.parseInt(idObj.toString());
        try {
            controller.deleteContact(id);
            loadContacts();
            JOptionPane.showMessageDialog(this, "Kontak berhasil dihapus.");
        } catch (SQLException ex) {
            showError("Gagal menghapus kontak: " + ex.getMessage());
        }
    }



    // Ambil ID dari model (kolom pertama di model menyimpan ID dari DB)
    Object idObj = model.getValueAt(selectedRow, 0);
    int id;
    try {
        id = Integer.parseInt(idObj.toString());
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, 
            "ID kontak tidak valid.", 
            "Kesalahan", 
            JOptionPane.ERROR_MESSAGE);
        return;
    }
}

                
                private void searchContact() {
        String keyword = txtPencarian.getText().trim();
        if (!keyword.isEmpty()) {
        try {
        List<Kontak> contacts = controller.searchContacts(keyword);
        model.setRowCount(0); // Bersihkan tabel
        for (Kontak contact : contacts) {
           model.addRow(new Object[]{
               contact.getId(),
               contact.getNama(),
               contact.getNomorTelepon(),
               contact.getKategori()
           });
       }

        if (contacts.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Tidak ada kontak ditemukan.");
        }
        } catch (SQLException ex) {
        showError(ex.getMessage());
        }
        } else {
        loadContacts();
        }
        }
 private void exportToCSV() {               
 JFileChooser fileChooser = new JFileChooser();
fileChooser.setDialogTitle("Simpan File CSV");
int userSelection = fileChooser.showSaveDialog(this);
if (userSelection == JFileChooser.APPROVE_OPTION) {
File fileToSave = fileChooser.getSelectedFile();
// Tambahkan ekstensi .csv jika pengguna tidak menambahkannya
if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
}
try (BufferedWriter writer = new BufferedWriter(new
FileWriter(fileToSave))) {
writer.write("ID,Nama,Nomor Telepon,Kategori\n");
for (int i = 0; i < model.getRowCount(); i++) {
    writer.write(
        model.getValueAt(i, 0) + "," +
        model.getValueAt(i, 1) + "," +
        model.getValueAt(i, 2) + "," +
        model.getValueAt(i, 3) + "\n"
    );
}

JOptionPane.showMessageDialog(this, "Data berhasil diekspor ke " + fileToSave.getAbsolutePath());
} catch (IOException ex) {
showError("Gagal menulis file: " + ex.getMessage());
}
}
}
private void importFromCSV() {
    showCSVGuide();
    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Apakah Anda yakin file CSV yang dipilih sudah sesuai dengan format?",
        "Konfirmasi Impor CSV",
        JOptionPane.YES_NO_OPTION
    );
    if (confirm != JOptionPane.YES_OPTION) return;

    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Pilih File CSV");

    int userSelection = fileChooser.showOpenDialog(this);
    if (userSelection != JFileChooser.APPROVE_OPTION) return;

    File fileToOpen = fileChooser.getSelectedFile();
    try (BufferedReader reader = new BufferedReader(new FileReader(fileToOpen))) {

        String line = reader.readLine(); // Baca header
        if (!validateCSVHeader(line)) {
            JOptionPane.showMessageDialog(this,
                "Format header CSV tidak valid.\nPastikan header adalah: ID,Nama,Nomor Telepon,Kategori",
                "Kesalahan CSV", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int rowCount = 0, errorCount = 0, duplicateCount = 0;
        StringBuilder errorLog = new StringBuilder("Baris dengan kesalahan:\n");

        while ((line = reader.readLine()) != null) {
            rowCount++;
            String[] data = line.split(",", -1); // Jaga agar kolom kosong tetap terbaca

            if (data.length != 4) {
                errorCount++;
                errorLog.append("Baris ").append(rowCount).append(": Format kolom tidak sesuai.\n");
                continue;
            }

            String nama = data[1].trim();
            String nomorTelepon = data[2].trim();
            String kategori = data[3].trim();

            if (nama.isEmpty() || nomorTelepon.isEmpty()) {
                errorCount++;
                errorLog.append("Baris ").append(rowCount).append(": Nama atau Nomor Telepon kosong.\n");
                continue;
            }

            if (!validatePhoneNumber(nomorTelepon)) {
                errorCount++;
                errorLog.append("Baris ").append(rowCount).append(": Nomor Telepon tidak valid.\n");
                continue;
            }

            try {
                if (controller.isDuplicatePhoneNumber(nomorTelepon, null)) {
                    duplicateCount++;
                    errorLog.append("Baris ").append(rowCount).append(": Kontak sudah ada.\n");
                    continue;
                }

                controller.addContact(nama, nomorTelepon, kategori);
            } catch (SQLException ex) {
                errorCount++;
                errorLog.append("Baris ").append(rowCount)
                        .append(": Gagal menyimpan ke database - ")
                        .append(ex.getMessage()).append("\n");
            }
        }

        loadContacts();

        if (errorCount > 0 || duplicateCount > 0) {
            errorLog.append("\nTotal baris dengan kesalahan: ").append(errorCount).append("\n");
            errorLog.append("Total baris duplikat: ").append(duplicateCount).append("\n");
            JOptionPane.showMessageDialog(this, errorLog.toString(),
                    "Kesalahan Impor", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Semua data berhasil diimpor.");
        }

    } catch (IOException ex) {
        showError("Gagal membaca file: " + ex.getMessage());
    }
}

private void showCSVGuide() {
String guideMessage = "Format CSV untuk impor data:\n" +
"- Header wajib: ID, Nama, Nomor Telepon, Kategori\n" +
"- ID dapat kosong (akan diisi otomatis)\n" +
"- Nama dan Nomor Telepon wajib diisi\n" +
"- Contoh isi file CSV:\n" +
" 1, Andi, 08123456789, Teman\n" +
" 2, Budi Doremi, 08567890123, Keluarga\n\n" +
"Pastikan file CSV sesuai format sebelum melakukan impor.";
JOptionPane.showMessageDialog(this, guideMessage, "Panduan Format CSV", JOptionPane.INFORMATION_MESSAGE);
}
private boolean validateCSVHeader(String header) {
    if (header == null) return false;
    String normalized = header.replaceAll("\\s+", "").toLowerCase();
    return normalized.equals("id,nama,nomortelepon,kategori");
}



    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton2 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtNomorTelepon = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cmbKategori = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btnTambah = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKontak = new javax.swing.JTable();
        txtPencarian = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();

        jButton2.setText("jButton2");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel2.setText("Nama Kontak");

        txtNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel3.setText("Nomor Telepon");

        txtNomorTelepon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomorTeleponActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel4.setText("Kategori");

        cmbKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Keluarga", "Teman", "Kantor", "" }));
        cmbKategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbKategoriActionPerformed(evt);
            }
        });

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jPanel3.setLayout(new java.awt.BorderLayout());

        btnTambah.setText("Tambah");
        btnTambah.setPreferredSize(new java.awt.Dimension(100, 29));
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnEdit.setText("Edit");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnExit.setText("Keluar");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        btnExport.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnExport.setText("Export");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        btnImport.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnImport.setText("Import");
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(99, 99, 99)
                        .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnEdit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnHapus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel3)
                                    .addGap(18, 18, 18))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addGap(73, 73, 73)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(33, 33, 33)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNomorTelepon)
                            .addComponent(cmbKategori, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtNama))))
                .addGap(20, 20, 20))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(109, 109, 109)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(btnExit)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(290, 290, 290)
                .addComponent(btnExport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImport))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtNomorTelepon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(cmbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnHapus))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnEdit)
                        .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExit)
                    .addComponent(btnExport)
                    .addComponent(btnImport)))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setText("APLIKASI PENGELOLAAN KONTAK");

        tblKontak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblKontak.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblKontakMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblKontak);

        txtPencarian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPencarianActionPerformed(evt);
            }
        });
        txtPencarian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPencarianKeyTyped(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel5.setText("Pencarian");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(36, 36, 36)
                        .addComponent(txtPencarian))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPencarian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaActionPerformed

    private void cmbKategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbKategoriActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbKategoriActionPerformed

    private void txtNomorTeleponActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomorTeleponActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomorTeleponActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtPencarianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPencarianActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPencarianActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        addContact();        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        System.exit(0);         // TODO add your handling code here:
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
            editContact();        // TODO add your handling code here:
    }//GEN-LAST:event_btnEditActionPerformed

    private void tblKontakMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblKontakMouseClicked
        int selectedRow = tblKontak.getSelectedRow();
        if (selectedRow != -1) {
        populateInputFields(selectedRow);
        }       // TODO add your handling code here:
    }//GEN-LAST:event_tblKontakMouseClicked

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        deleteContact();   // TODO add your handling code here:
    }//GEN-LAST:event_btnHapusActionPerformed

    private void txtPencarianKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPencarianKeyTyped
      searchContact();  // TODO add your handling code here:
    }//GEN-LAST:event_txtPencarianKeyTyped

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        exportToCSV();// TODO add your handling code here:
    }//GEN-LAST:event_btnExportActionPerformed

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        importFromCSV();// TODO add your handling code here:
    }//GEN-LAST:event_btnImportActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PengelolaanKontakFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnTambah;
    private javax.swing.JComboBox<String> cmbKategori;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblKontak;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtNomorTelepon;
    private javax.swing.JTextField txtPencarian;
    // End of variables declaration//GEN-END:variables
}
