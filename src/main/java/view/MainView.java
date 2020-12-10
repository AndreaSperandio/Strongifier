package view;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;

import view.component.STButton;
import view.util.STColor;
import view.util.STLocalizator;
import view.util.STMessage;
import view.util.STResource;

public class MainView extends JFrame {
	private static final long serialVersionUID = 846132440578478084L;

	private static final STLocalizator LOC = new STLocalizator(MainView.class);
	private static final String DESKTOP_FOLDER = System.getProperty("user.home") + File.separator + "Desktop";
	private static final String FILE_EXTENSION = ".docx";

	private final JLabel lblInstruction = new JLabel(MainView.LOC.getRes("lblInstruction"));
	private final JLabel lblDocument = new JLabel(MainView.LOC.getRes("lblDocument"));
	private final STButton btnDocument = new STButton(MainView.LOC.getRes("btnLoad"));
	private final JLabel lblDocumentFile = new JLabel();
	private final STButton btnTransform = new STButton(STResource.getStartImage());
	private final JLabel lblTransform = new JLabel(MainView.LOC.getRes("lblTransform"));
	private final JLabel lblExportPath = new JLabel(MainView.LOC.getRes("lblExportPath"));
	private final JLabel lblExportPathWrn = new JLabel(MainView.LOC.getRes("lblExportPathWrn"));
	private final STButton btnExportPath = new STButton(MainView.LOC.getRes("btnExportPath"));
	private final JLabel lblExportFile = new JLabel();
	private final STButton btnExport = new STButton(STResource.getExportImage());
	private final JLabel lblExport = new JLabel(MainView.LOC.getRes("lblExport"));
	private final JLabel lblAuthor = new JLabel(MainView.LOC.getRes("lblAuthor"));

	private File documentFile = null;
	private File exportFile = null;
	private XWPFDocument outputDoc = null;

	public MainView() {
		this.setup();
		this.init();
	}

	private void setup() {
		this.setTitle(MainView.LOC.getRes("title"));
		final Dimension dimension = new Dimension(510, 450);// Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(dimension);
		this.setPreferredSize(dimension);
		// this.setExtendedState(Frame.MAXIMIZED_BOTH);
		this.setIconImages(STResource.getLogoIcons());
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				//if (TMMessage.showConfirmWarnDialog(MainView.this, MainView.LOC.getRes("cnfExit"))) {
				MainView.this.dispose();
				System.exit(0);
				//}
			}
		});
		this.setLayout(null);

		this.add(this.lblInstruction);
		this.add(this.lblDocument);
		this.add(this.btnDocument);
		this.add(this.lblDocumentFile);
		this.add(this.btnTransform);
		this.add(this.lblTransform);
		this.add(this.lblExportPath);
		this.add(this.lblExportPathWrn);
		this.add(this.btnExportPath);
		this.add(this.lblExportFile);
		this.add(this.btnExport);
		this.add(this.lblExport);
		this.add(this.lblAuthor);

		final int height = 20;
		final int margin = height + 10;
		final int x = 20;
		int y = 10;
		this.lblInstruction.setBounds(x, y, 560, height * 3);
		y += 80;
		this.lblDocument.setBounds(x, y, 500, height);
		y += margin;
		this.btnDocument.setBounds(x, y, 100, height);
		this.lblDocumentFile.setBounds(x + 120, y, 400, height);
		y += 50;
		this.btnTransform.setBounds(x, y, 35, height + 10);
		this.lblTransform.setBounds(x + 45, y, 200, height + 10);
		y += 70;
		this.lblExportPath.setBounds(x, y, 100, height);
		this.lblExportPathWrn.setBounds(x + 120, y, 400, height);
		y += margin;
		this.btnExportPath.setBounds(x, y, 100, height);
		this.lblExportFile.setBounds(x + 120, y, 400, height);
		y += 50;
		this.btnExport.setBounds(x, y, 35, height + 10);
		this.lblExport.setBounds(x + 45, y, 200, height + 10);
		y += 50;
		this.lblAuthor.setBounds(dimension.width - 150, y, 120, height);

		this.lblDocument.setToolTipText(MainView.LOC.getRes("lblDocumentToolTip"));
		this.lblTransform.setToolTipText(MainView.LOC.getRes("lblTransformToolTip"));
		this.lblExportPath.setToolTipText(MainView.LOC.getRes("lblExportPathToolTip"));

		this.lblDocumentFile.setForeground(STColor.LBL_BLUE);
		this.lblExportFile.setForeground(STColor.LBL_BLUE);
		this.lblExportPathWrn.setForeground(STColor.LBL_ORANGE);
		this.lblAuthor.setForeground(STColor.LBL_BLUE);

		this.btnDocument.addActionListener(e -> {
			this.btnFileChooserActionPerformed(this.btnDocument, MainView.LOC.getRes("jfcDocument"),
					this.lblDocumentFile);
			this.updateGraphics();
		});
		this.btnTransform.addActionListener(e -> {
			this.btnMatchActionPerformed();
			this.updateGraphics();
		});
		this.btnExportPath.addActionListener(e -> {
			this.btnFileChooserActionPerformed(this.btnExport, MainView.LOC.getRes("jfcExport"), this.lblExportFile);
			this.updateGraphics();
		});
		this.btnExport.addActionListener(e -> this.btnExportActionPerformed());
	}

	private void init() {
		this.updateGraphics();
		this.setVisible(true);
	}

	private void updateGraphics() {
		this.btnTransform.setEnabled(this.documentFile != null);
		this.btnExportPath.setEnabled(this.outputDoc != null);
		this.btnExport.setEnabled(this.exportFile != null);

		this.lblExportPathWrn.setVisible(this.checkFile(this.exportFile, false));
	}

	private void btnFileChooserActionPerformed(final STButton caller, final String title, final JLabel label) {
		final FileNameExtensionFilter filter = new FileNameExtensionFilter("MS Word Document (2007+)", "docx");
		final JFileChooser jfc = new JFileChooser(MainView.DESKTOP_FOLDER);
		jfc.setFileFilter(filter);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setMultiSelectionEnabled(false);
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setDialogTitle(title);

		final int retVaule = jfc.showOpenDialog(caller);
		if (retVaule == JFileChooser.APPROVE_OPTION) {
			File file = jfc.getSelectedFile();
			if (caller == this.btnDocument) {
				this.documentFile = file;
				this.outputDoc = null;
			} else {
				if (!file.getName().endsWith(MainView.FILE_EXTENSION)) {
					file = new File(file.getAbsolutePath() + MainView.FILE_EXTENSION);
				}
				this.exportFile = file;
			}
			label.setText(file.getName());
		} else if (retVaule == JFileChooser.CANCEL_OPTION) {
			if (caller == this.btnDocument) {
				this.documentFile = null;
				this.outputDoc = null;
			} else {
				this.exportFile = null;
			}
			label.setText(null);
		}
	}

	private void btnMatchActionPerformed() {
		if (!this.checkParams()) {
			return;
		}

		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		final int transformed = this.scanFile(this.documentFile);
		this.setCursor(Cursor.getDefaultCursor());

		STMessage.showInfoDialog(this, MainView.LOC.getRes("infNTransformed", transformed));
		this.updateGraphics();
	}

	private void btnExportActionPerformed() {
		if (!this.checkExportParams()) {
			return;
		}

		if (this.writeToFile(this.exportFile, this.outputDoc)) {
			if (STMessage.showConfirmDialog(this, MainView.LOC.getRes("cnfExported"))) {
				try {
					Desktop.getDesktop().open(this.exportFile);
				} catch (final IOException e) {
					STMessage.showErrDialog(this, "errInternalError");
					e.printStackTrace();
				}
			}

			this.lblDocumentFile.setText(null);
			this.lblExportFile.setText(null);
			this.documentFile = null;
			this.exportFile = null;
			this.outputDoc = null;
			this.updateGraphics();
		}
	}

	private boolean checkParams() {
		if (!this.checkFile(this.documentFile, false)) {
			STMessage.showErrDialog(this, MainView.LOC.getRes("errDocumentFile"));
			return false;
		}

		return true;
	}

	private boolean checkExportParams() {
		if (!this.checkFile(this.exportFile, true)) {
			STMessage.showErrDialog(this, MainView.LOC.getRes("errExportPath"));
			return false;
		}

		return true;
	}

	private boolean checkFile(final File file, final boolean createIfAbsent) {
		if (file == null || !file.exists() || !file.isFile()) {
			if (createIfAbsent) {
				try {
					file.createNewFile();
					return true;
				} catch (final IOException e) {
					STMessage.showErrDialog(this, "errInternalError");
					e.printStackTrace();
				}
			}
			return false;
		}

		return file.getName().endsWith(MainView.FILE_EXTENSION);
	}

	private int scanFile(final File file) {
		int count = 0;
		if (file == null) {
			return count;
		}

		this.outputDoc = new XWPFDocument();

		try (FileInputStream fis = new FileInputStream(file);
				XWPFDocument document = new XWPFDocument(OPCPackage.open(fis));) {
			final String startStrong = "<strong>";
			final String endStrong = "</strong>";
			boolean lastBold = false;

			final List<XWPFParagraph> paragraphs = document.getParagraphs();
			XWPFParagraph outputParagraph;
			List<XWPFRun> runs;
			XWPFRun run;
			XWPFRun outputRun;
			CTR ctr;

			for (final XWPFParagraph paragraph : paragraphs) {
				outputParagraph = this.outputDoc.createParagraph();
				runs = paragraph.getRuns();

				for (int r = 0; r < runs.size(); r++) {
					run = runs.get(r);
					ctr = run.getCTR();

					if (run.isBold() && !lastBold) {
						outputRun = outputParagraph.createRun();
						outputRun.getCTR().set(ctr);
						outputRun.setBold(true);
						outputRun.setText(startStrong, 0);
						lastBold = true;
						count++;
					} else if (!run.isBold() && lastBold) {
						outputRun = outputParagraph.createRun();
						outputRun.getCTR().set(ctr);
						outputRun.setBold(true);
						outputRun.setText(endStrong, 0);
						lastBold = false;
					}

					outputRun = outputParagraph.createRun();
					outputRun.getCTR().set(ctr);

					//Add endStrong tag at the end of the paragraph, if necessary
					if (lastBold && r == runs.size() - 1) {
						outputRun = outputParagraph.createRun();
						outputRun.getCTR().set(ctr);
						outputRun.setBold(true);
						outputRun.setText(endStrong, 0);
						lastBold = false;
					}
				}
			}
		} catch (final IOException e) {
			STMessage.showErrDialog(this, MainView.LOC.getRes("errInternalError"));
			e.printStackTrace();
			this.outputDoc = null;
		} catch (final InvalidFormatException e) {
			STMessage.showErrDialog(this, MainView.LOC.getRes("errInternalError"));
			e.printStackTrace();
			this.outputDoc = null;
		}
		return count;
	}

	private boolean writeToFile(final File file, final XWPFDocument document) {
		if (file == null) {
			return false;
		}

		try (FileOutputStream fos = new FileOutputStream(file);) {
			document.write(fos);
			document.close();
			return true;
		} catch (final FileNotFoundException e) {
			STMessage.showErrDialog(this, MainView.LOC.getRes("errFileAlreadyInUse"));
			e.printStackTrace();
		} catch (final IOException e) {
			STMessage.showErrDialog(this, MainView.LOC.getRes("errInternalError"));
			e.printStackTrace();
		}
		return false;
	}

	public static void main(final String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				final MainView frame = new MainView();
				frame.setVisible(true);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		});
	}
}
