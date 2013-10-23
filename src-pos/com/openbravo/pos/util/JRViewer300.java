/*
 * ============================================================================
 * GNU Lesser General Public License
 * ============================================================================
 *
 * JasperReports - Free Java report-generating library.
 * Copyright (C) 2001-2006 JasperSoft Corporation http://www.jaspersoft.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 *
 * JasperSoft Corporation
 * 303 Second Street, Suite 450 North
 * San Francisco, CA 94107
 * http://www.jaspersoft.com
 */

/*
 * Contributors:
 * Ryan Johnson - delscovich@users.sourceforge.net
 * Carlton Moore - cmoore79@users.sourceforge.net
 *  Petr Michalek - pmichalek@users.sourceforge.net
 */

//    Portions:
//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2009 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
//    author adrian romero
// This class is a copy of net.sf.jasperreports.view.JRViewer
// The modifications are:
// The loadJasperPrint() method 
// And the redesign of the design properties of the toolbar
// Nothing else.

package com.openbravo.pos.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.JRImageMapRenderer;
import net.sf.jasperreports.engine.JRPrintAnchorIndex;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintImageAreaHyperlink;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRRenderable;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.engine.export.JRGraphics2DExporterParameter;
import net.sf.jasperreports.engine.print.JRPrinterAWT;
import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRProperties;
import net.sf.jasperreports.engine.xml.JRPrintXmlLoader;
import net.sf.jasperreports.view.JRHyperlinkListener;
import net.sf.jasperreports.view.JRSaveContributor;
import net.sf.jasperreports.view.save.JRPrintSaveContributor;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: JRViewer300.java 2160 2008-04-29 11:31:51Z lucianc $
 */
public class JRViewer300 extends javax.swing.JPanel implements JRHyperlinkListener
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 * Maximum size (in pixels) of a buffered image that would be used by {@link JRViewer300 JRViewer300} to render a report page.
	 * <p>
	 * If rendering a report page would require an image larger than this threshold
	 * (i.e. image width x image height > maximum size), the report page will be rendered directly on the viewer component.
	 * </p>
	 * <p>
	 * If this property is zero or negative, buffered images will never be user to render a report page.
	 * By default, this property is set to 0.
	 * </p>
	 */
	public static final String VIEWER_RENDER_BUFFER_MAX_SIZE = JRProperties.PROPERTY_PREFIX + "viewer.render.buffer.max.size";

	/**
	 *
	 */
	protected static final int TYPE_FILE_NAME = 1;
	protected static final int TYPE_INPUT_STREAM = 2;
	protected static final int TYPE_OBJECT = 3;

	/**
	 * The DPI of the generated report.
	 */
	public static final int REPORT_RESOLUTION = 72;

	protected float MIN_ZOOM = 0.5f;
	protected float MAX_ZOOM = 10f;
	protected int zooms[] = {50, 75, 100, 125, 150, 175, 200, 250, 400, 800};
	protected int defaultZoomIndex = 2;

	protected int type = TYPE_FILE_NAME;
	protected boolean isXML = false;
	protected String reportFileName = null;
	JasperPrint jasperPrint = null;
	private int pageIndex = 0;
	private boolean pageError;
	protected float zoom = 0f;

	private JRGraphics2DExporter exporter = null;

	/**
	 * the screen resolution.
	 */
	private int screenResolution = REPORT_RESOLUTION;

	/**
	 * the zoom ration adjusted to the screen resolution.
	 */
	protected float realZoom = 0f;

	private DecimalFormat zoomDecimalFormat = new DecimalFormat("#.##");
	private ResourceBundle resourceBundle = null;

	private int downX = 0;
	private int downY = 0;

	private java.util.List hyperlinkListeners = new ArrayList();
	private Map linksMap = new HashMap();
	private MouseListener mouseListener =
		new java.awt.event.MouseAdapter()
		{
			public void mouseClicked(java.awt.event.MouseEvent evt)
			{
				hyperlinkClicked(evt);
			}
		};

	protected KeyListener keyNavigationListener =
		new KeyListener() {
			public void keyTyped(KeyEvent evt)
			{
			}
			public void keyPressed(KeyEvent evt)
			{
				keyNavigate(evt);
			}
			public void keyReleased(KeyEvent evt)
			{
			}
		};

	protected List saveContributors = new ArrayList();
	protected File lastFolder = null;
	protected JRSaveContributor lastSaveContributor = null;

	/** Creates new form JRViewer300 */
	public JRViewer300(String fileName, boolean isXML) throws JRException
	{
		this(fileName, isXML, null);
	}


	/** Creates new form JRViewer300 */
	public JRViewer300(InputStream is, boolean isXML) throws JRException
	{
		this(is, isXML, null);
	}


	/** Creates new form JRViewer300 */
	public JRViewer300(JasperPrint jrPrint)
	{
		this(jrPrint, null);
	}


	/** Creates new form JRViewer300 */
	public JRViewer300(String fileName, boolean isXML, Locale locale) throws JRException
	{
		this(fileName, isXML, locale, null);
	}


	/** Creates new form JRViewer300 */
	public JRViewer300(InputStream is, boolean isXML, Locale locale) throws JRException
	{
		this(is, isXML, locale, null);
	}


	/** Creates new form JRViewer300 */
	public JRViewer300(JasperPrint jrPrint, Locale locale)
	{
		this(jrPrint, locale, null);
	}


	/** Creates new form JRViewer300 */
	public JRViewer300(String fileName, boolean isXML, Locale locale, ResourceBundle resBundle) throws JRException
	{
		initResources(locale, resBundle);

		setScreenDetails();

		setZooms();

		initComponents();

		loadReport(fileName, isXML);

		cmbZoom.setSelectedIndex(defaultZoomIndex);

		initSaveContributors();

		addHyperlinkListener(this);
	}


	/** Creates new form JRViewer300 */
	public JRViewer300(InputStream is, boolean isXML, Locale locale, ResourceBundle resBundle) throws JRException
	{
		initResources(locale, resBundle);

		setScreenDetails();

		setZooms();

		initComponents();

		loadReport(is, isXML);

		cmbZoom.setSelectedIndex(defaultZoomIndex);

		initSaveContributors();

		addHyperlinkListener(this);
	}


	/** Creates new form JRViewer300 */
	public JRViewer300(JasperPrint jrPrint, Locale locale, ResourceBundle resBundle)
	{
		initResources(locale, resBundle);

		setScreenDetails();

		setZooms();

		initComponents();

		loadReport(jrPrint);

		cmbZoom.setSelectedIndex(defaultZoomIndex);

		initSaveContributors();

		addHyperlinkListener(this);
	}
        
        public void loadJasperPrint(JasperPrint jrPrint) {
            
		loadReport(jrPrint);
		setZoomRatio(zooms[defaultZoomIndex] / 100f);
                cmbZoomItemStateChanged(null);
                refreshPage();
        }


	private void setScreenDetails()
	{
		screenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
	}


	/**
	 *
	 */
	public void clear()
	{
		emptyContainer(this);
		jasperPrint = null;
	}


	/**
	 *
	 */
	protected void setZooms()
	{
	}


	/**
	 *
	 */
	public void addSaveContributor(JRSaveContributor contributor)
	{
		saveContributors.add(contributor);
	}


	/**
	 *
	 */
	public void removeSaveContributor(JRSaveContributor contributor)
	{
		saveContributors.remove(contributor);
	}


	/**
	 *
	 */
	public JRSaveContributor[] getSaveContributors()
	{
		return (JRSaveContributor[])saveContributors.toArray(new JRSaveContributor[saveContributors.size()]);
	}


	/**
	 * Replaces the save contributors with the ones provided as parameter. 
	 */
	public void setSaveContributors(JRSaveContributor[] saveContributors)
	{
		this.saveContributors = new ArrayList();
		if (saveContributors != null)
		{
			this.saveContributors.addAll(Arrays.asList(saveContributors));
		}
	}


	/**
	 *
	 */
	public void addHyperlinkListener(JRHyperlinkListener listener)
	{
		hyperlinkListeners.add(listener);
	}


	/**
	 *
	 */
	public void removeHyperlinkListener(JRHyperlinkListener listener)
	{
		hyperlinkListeners.remove(listener);
	}


	/**
	 *
	 */
	public JRHyperlinkListener[] getHyperlinkListeners()
	{
		return (JRHyperlinkListener[])hyperlinkListeners.toArray(new JRHyperlinkListener[hyperlinkListeners.size()]);
	}


	/**
	 *
	 */
	protected void initResources(Locale locale, ResourceBundle resBundle)
	{
		if (locale != null)
			setLocale(locale);
		else
			setLocale(Locale.getDefault());

		if (resBundle == null)
		{
			this.resourceBundle = ResourceBundle.getBundle("net/sf/jasperreports/view/viewer", getLocale());
		}
		else
		{
			this.resourceBundle = resBundle;
		}
	}


	/**
	 *
	 */
	protected String getBundleString(String key)
	{
		return resourceBundle.getString(key);
	}


	/**
	 *
	 */
	protected void initSaveContributors()
	{
		final String[] DEFAULT_CONTRIBUTORS =
			{
				"net.sf.jasperreports.view.save.JRPrintSaveContributor",
				"net.sf.jasperreports.view.save.JRPdfSaveContributor",
				"net.sf.jasperreports.view.save.JRRtfSaveContributor",
				"net.sf.jasperreports.view.save.JROdtSaveContributor",
				"net.sf.jasperreports.view.save.JRHtmlSaveContributor",
				"net.sf.jasperreports.view.save.JRSingleSheetXlsSaveContributor",
				"net.sf.jasperreports.view.save.JRMultipleSheetsXlsSaveContributor",
				"net.sf.jasperreports.view.save.JRCsvSaveContributor",
				"net.sf.jasperreports.view.save.JRXmlSaveContributor",
				"net.sf.jasperreports.view.save.JREmbeddedImagesXmlSaveContributor"
			};

		for(int i = 0; i < DEFAULT_CONTRIBUTORS.length; i++)
		{
			try
			{
				Class saveContribClass = JRClassLoader.loadClassForName(DEFAULT_CONTRIBUTORS[i]);
				Constructor constructor = saveContribClass.getConstructor(new Class[]{Locale.class, ResourceBundle.class});
				JRSaveContributor saveContrib = (JRSaveContributor)constructor.newInstance(new Object[]{getLocale(), resourceBundle});
				saveContributors.add(saveContrib);
			}
			catch (Exception e)
			{
			}
		}
	}


	/**
	 *
	 */
	public void gotoHyperlink(JRPrintHyperlink hyperlink)
	{
		switch(hyperlink.getHyperlinkType())
		{
			case JRHyperlink.HYPERLINK_TYPE_REFERENCE :
			{
				if (isOnlyHyperlinkListener())
				{
					System.out.println("Hyperlink reference : " + hyperlink.getHyperlinkReference());
					System.out.println("Implement your own JRHyperlinkListener to manage this type of event.");
				}
				break;
			}
			case JRHyperlink.HYPERLINK_TYPE_LOCAL_ANCHOR :
			{
				if (hyperlink.getHyperlinkAnchor() != null)
				{
					Map anchorIndexes = jasperPrint.getAnchorIndexes();
					JRPrintAnchorIndex anchorIndex = (JRPrintAnchorIndex)anchorIndexes.get(hyperlink.getHyperlinkAnchor());
					if (anchorIndex.getPageIndex() != pageIndex)
					{
						setPageIndex(anchorIndex.getPageIndex());
						refreshPage();
					}
					Container container = pnlInScroll.getParent();
					if (container instanceof JViewport)
					{
						JViewport viewport = (JViewport) container;

						int newX = (int)(anchorIndex.getElementAbsoluteX() * realZoom);
						int newY = (int)(anchorIndex.getElementAbsoluteY() * realZoom);

						int maxX = pnlInScroll.getWidth() - viewport.getWidth();
						int maxY = pnlInScroll.getHeight() - viewport.getHeight();

						if (newX < 0)
						{
							newX = 0;
						}
						if (newX > maxX)
						{
							newX = maxX;
						}
						if (newY < 0)
						{
							newY = 0;
						}
						if (newY > maxY)
						{
							newY = maxY;
						}

						viewport.setViewPosition(new Point(newX, newY));
					}
				}

				break;
			}
			case JRHyperlink.HYPERLINK_TYPE_LOCAL_PAGE :
			{
				int page = pageIndex + 1;
				if (hyperlink.getHyperlinkPage() != null)
				{
					page = hyperlink.getHyperlinkPage().intValue();
				}

				if (page >= 1 && page <= jasperPrint.getPages().size() && page != pageIndex + 1)
				{
					setPageIndex(page - 1);
					refreshPage();
					Container container = pnlInScroll.getParent();
					if (container instanceof JViewport)
					{
						JViewport viewport = (JViewport) container;
						viewport.setViewPosition(new Point(0, 0));
					}
				}

				break;
			}
			case JRHyperlink.HYPERLINK_TYPE_REMOTE_ANCHOR :
			{
				if (isOnlyHyperlinkListener())
				{
					System.out.println("Hyperlink reference : " + hyperlink.getHyperlinkReference());
					System.out.println("Hyperlink anchor    : " + hyperlink.getHyperlinkAnchor());
					System.out.println("Implement your own JRHyperlinkListener to manage this type of event.");
				}
				break;
			}
			case JRHyperlink.HYPERLINK_TYPE_REMOTE_PAGE :
			{
				if (isOnlyHyperlinkListener())
				{
					System.out.println("Hyperlink reference : " + hyperlink.getHyperlinkReference());
					System.out.println("Hyperlink page      : " + hyperlink.getHyperlinkPage());
					System.out.println("Implement your own JRHyperlinkListener to manage this type of event.");
				}
				break;
			}
			case JRHyperlink.HYPERLINK_TYPE_CUSTOM:
			{
				if (isOnlyHyperlinkListener())
				{
					System.out.println("Hyperlink of type " + hyperlink.getLinkType());
					System.out.println("Implement your own JRHyperlinkListener to manage this type of event.");
				}
				break;
			}
			case JRHyperlink.HYPERLINK_TYPE_NONE :
			default :
			{
				break;
			}
		}
	}


	protected boolean isOnlyHyperlinkListener()
	{
		int listenerCount;
		if (hyperlinkListeners == null)
		{
			listenerCount = 0;
		}
		else
		{
			listenerCount = hyperlinkListeners.size();
			if (hyperlinkListeners.contains(this))
			{
				--listenerCount;
			}
		}
		return listenerCount == 0;
	}


	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlMain = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        scrollPane.getHorizontalScrollBar().setUnitIncrement(5);
        scrollPane.getVerticalScrollBar().setUnitIncrement(5);
        pnlInScroll = new javax.swing.JPanel();
        pnlPage = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        pnlLinks = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        lblPage = new PageRenderer(this);
        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnReload = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnActualSize = new javax.swing.JToggleButton();
        btnFitPage = new javax.swing.JToggleButton();
        btnFitWidth = new javax.swing.JToggleButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnZoomIn = new javax.swing.JButton();
        cmbZoom = new javax.swing.JComboBox();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for(int i = 0; i < zooms.length; i++)
        {
            model.addElement("" + zooms[i] + "%");
        }
        cmbZoom.setModel(model);
        btnZoomOut = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnFirst = new javax.swing.JButton();
        btnPrevious = new javax.swing.JButton();
        txtGoTo = new javax.swing.JTextField();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        lblStatus = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(450, 150));
        setPreferredSize(new java.awt.Dimension(450, 150));
        setLayout(new java.awt.BorderLayout());

        pnlMain.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                pnlMainComponentResized(evt);
            }
        });
        pnlMain.setLayout(new java.awt.BorderLayout());

        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        pnlInScroll.setLayout(new java.awt.GridBagLayout());

        pnlPage.setMinimumSize(new java.awt.Dimension(100, 100));
        pnlPage.setPreferredSize(new java.awt.Dimension(100, 100));
        pnlPage.setLayout(new java.awt.BorderLayout());

        jPanel4.setMinimumSize(new java.awt.Dimension(100, 120));
        jPanel4.setPreferredSize(new java.awt.Dimension(100, 120));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        pnlLinks.setMinimumSize(new java.awt.Dimension(5, 5));
        pnlLinks.setOpaque(false);
        pnlLinks.setPreferredSize(new java.awt.Dimension(5, 5));
        pnlLinks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlLinksMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pnlLinksMouseReleased(evt);
            }
        });
        pnlLinks.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                pnlLinksMouseDragged(evt);
            }
        });
        pnlLinks.setLayout(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel4.add(pnlLinks, gridBagConstraints);

        jPanel5.setBackground(java.awt.Color.gray);
        jPanel5.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanel5.setPreferredSize(new java.awt.Dimension(5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanel4.add(jPanel5, gridBagConstraints);

        jPanel6.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanel6.setPreferredSize(new java.awt.Dimension(5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        jPanel4.add(jPanel6, gridBagConstraints);

        jPanel7.setBackground(java.awt.Color.gray);
        jPanel7.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanel7.setPreferredSize(new java.awt.Dimension(5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel4.add(jPanel7, gridBagConstraints);

        jPanel8.setBackground(java.awt.Color.gray);
        jPanel8.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanel8.setPreferredSize(new java.awt.Dimension(5, 5));

        jLabel1.setText("jLabel1");
        jPanel8.add(jLabel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        jPanel4.add(jPanel8, gridBagConstraints);

        jPanel9.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanel9.setPreferredSize(new java.awt.Dimension(5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanel4.add(jPanel9, gridBagConstraints);

        lblPage.setBackground(java.awt.Color.white);
        lblPage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblPage.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(lblPage, gridBagConstraints);

        pnlPage.add(jPanel4, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInScroll.add(pnlPage, gridBagConstraints);

        scrollPane.setViewportView(pnlInScroll);

        pnlMain.add(scrollPane, java.awt.BorderLayout.CENTER);

        jToolBar1.setFloatable(false);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/filesave.png"))); // NOI18N
        btnSave.setToolTipText(getBundleString("save"));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/yast_printer.png"))); // NOI18N
        btnPrint.setToolTipText(getBundleString("print"));
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

        btnReload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/reload.png"))); // NOI18N
        btnReload.setToolTipText(getBundleString("reload"));
        btnReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReloadActionPerformed(evt);
            }
        });
        jToolBar1.add(btnReload);
        jToolBar1.add(jSeparator1);

        btnActualSize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/mime.png"))); // NOI18N
        btnActualSize.setToolTipText(getBundleString("actual.size"));
        btnActualSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualSizeActionPerformed(evt);
            }
        });
        jToolBar1.add(btnActualSize);

        btnFitPage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/mime2.png"))); // NOI18N
        btnFitPage.setToolTipText(getBundleString("fit.page"));
        btnFitPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFitPageActionPerformed(evt);
            }
        });
        jToolBar1.add(btnFitPage);

        btnFitWidth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/mime3.png"))); // NOI18N
        btnFitWidth.setToolTipText(getBundleString("fit.width"));
        btnFitWidth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFitWidthActionPerformed(evt);
            }
        });
        jToolBar1.add(btnFitWidth);
        jToolBar1.add(jSeparator2);

        btnZoomIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/viewmag+.png"))); // NOI18N
        btnZoomIn.setToolTipText(getBundleString("zoom.in"));
        btnZoomIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZoomInActionPerformed(evt);
            }
        });
        jToolBar1.add(btnZoomIn);

        cmbZoom.setEditable(true);
        cmbZoom.setToolTipText(getBundleString("zoom.ratio"));
        cmbZoom.setMaximumSize(new java.awt.Dimension(80, 23));
        cmbZoom.setMinimumSize(new java.awt.Dimension(80, 23));
        cmbZoom.setPreferredSize(new java.awt.Dimension(80, 23));
        cmbZoom.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbZoomItemStateChanged(evt);
            }
        });
        cmbZoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbZoomActionPerformed(evt);
            }
        });
        jToolBar1.add(cmbZoom);

        btnZoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/viewmag-.png"))); // NOI18N
        btnZoomOut.setToolTipText(getBundleString("zoom.out"));
        btnZoomOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZoomOutActionPerformed(evt);
            }
        });
        jToolBar1.add(btnZoomOut);
        jToolBar1.add(jSeparator3);

        btnFirst.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/2leftarrow.png"))); // NOI18N
        btnFirst.setToolTipText(getBundleString("first.page"));
        btnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstActionPerformed(evt);
            }
        });
        jToolBar1.add(btnFirst);

        btnPrevious.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/1leftarrow.png"))); // NOI18N
        btnPrevious.setToolTipText(getBundleString("previous.page"));
        btnPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrevious);

        txtGoTo.setToolTipText(getBundleString("go.to.page"));
        txtGoTo.setMaximumSize(new java.awt.Dimension(40, 23));
        txtGoTo.setMinimumSize(new java.awt.Dimension(40, 23));
        txtGoTo.setPreferredSize(new java.awt.Dimension(40, 23));
        txtGoTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGoToActionPerformed(evt);
            }
        });
        jToolBar1.add(txtGoTo);

        btnNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/1rightarrow.png"))); // NOI18N
        btnNext.setToolTipText(getBundleString("next.page"));
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNext);

        btnLast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/2rightarrow.png"))); // NOI18N
        btnLast.setToolTipText(getBundleString("last.page"));
        btnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastActionPerformed(evt);
            }
        });
        jToolBar1.add(btnLast);
        jToolBar1.add(jSeparator4);

        lblStatus.setText("Page i of n");
        jToolBar1.add(lblStatus);

        pnlMain.add(jToolBar1, java.awt.BorderLayout.NORTH);

        add(pnlMain, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

	void txtGoToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGoToActionPerformed
		try
		{
			int pageNumber = Integer.parseInt(txtGoTo.getText());
			if (
				pageNumber != pageIndex + 1
				&& pageNumber > 0
				&& pageNumber <= jasperPrint.getPages().size()
				)
			{
				setPageIndex(pageNumber - 1);
				refreshPage();
			}
		}
		catch(NumberFormatException e)
		{
		}
	}//GEN-LAST:event_txtGoToActionPerformed

	void cmbZoomItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbZoomItemStateChanged
		// Add your handling code here:
		btnActualSize.setSelected(false);
		btnFitPage.setSelected(false);
		btnFitWidth.setSelected(false);
	}//GEN-LAST:event_cmbZoomItemStateChanged

	void pnlMainComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_pnlMainComponentResized
		// Add your handling code here:
		if (btnFitPage.isSelected())
		{
			fitPage();
			btnFitPage.setSelected(true);
		}
		else if (btnFitWidth.isSelected())
		{
			setRealZoomRatio(((float)pnlInScroll.getVisibleRect().getWidth() - 20f) / jasperPrint.getPageWidth());
			btnFitWidth.setSelected(true);
		}

	}//GEN-LAST:event_pnlMainComponentResized

	void btnActualSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualSizeActionPerformed
		// Add your handling code here:
		if (btnActualSize.isSelected())
		{
			btnFitPage.setSelected(false);
			btnFitWidth.setSelected(false);
			cmbZoom.setSelectedIndex(-1);
			setZoomRatio(1);
			btnActualSize.setSelected(true);
		}
	}//GEN-LAST:event_btnActualSizeActionPerformed

	void btnFitWidthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFitWidthActionPerformed
		// Add your handling code here:
		if (btnFitWidth.isSelected())
		{
			btnActualSize.setSelected(false);
			btnFitPage.setSelected(false);
			cmbZoom.setSelectedIndex(-1);
			setRealZoomRatio(((float)pnlInScroll.getVisibleRect().getWidth() - 20f) / jasperPrint.getPageWidth());
			btnFitWidth.setSelected(true);
		}
	}//GEN-LAST:event_btnFitWidthActionPerformed

	void btnFitPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFitPageActionPerformed
		// Add your handling code here:
		if (btnFitPage.isSelected())
		{
			btnActualSize.setSelected(false);
			btnFitWidth.setSelected(false);
			cmbZoom.setSelectedIndex(-1);
			fitPage();
			btnFitPage.setSelected(true);
		}
	}//GEN-LAST:event_btnFitPageActionPerformed

	void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
		// Add your handling code here:

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setLocale(this.getLocale());
		fileChooser.updateUI();
		for(int i = 0; i < saveContributors.size(); i++)
		{
			fileChooser.addChoosableFileFilter((JRSaveContributor)saveContributors.get(i));
		}

		if (saveContributors.contains(lastSaveContributor))
		{
			fileChooser.setFileFilter(lastSaveContributor);
		}
		else if (saveContributors.size() > 0)
		{
			fileChooser.setFileFilter((JRSaveContributor)saveContributors.get(0));
		}
		
		if (lastFolder != null)
		{
			fileChooser.setCurrentDirectory(lastFolder);
		}
		
		int retValue = fileChooser.showSaveDialog(this);
		if (retValue == JFileChooser.APPROVE_OPTION)
		{
			FileFilter fileFilter = fileChooser.getFileFilter();
			File file = fileChooser.getSelectedFile();
			
			lastFolder = file.getParentFile();

			JRSaveContributor contributor = null;

			if (fileFilter instanceof JRSaveContributor)
			{
				contributor = (JRSaveContributor)fileFilter;
			}
			else
			{
				int i = 0;
				while(contributor == null && i < saveContributors.size())
				{
					contributor = (JRSaveContributor)saveContributors.get(i++);
					if (!contributor.accept(file))
					{
						contributor = null;
					}
				}

				if (contributor == null)
				{
					contributor = new JRPrintSaveContributor(getLocale(), this.resourceBundle);
				}
			}

			lastSaveContributor = contributor;
			
			try
			{
				contributor.save(jasperPrint, file);
			}
			catch (JRException e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, getBundleString("error.saving"));
			}
		}
	}//GEN-LAST:event_btnSaveActionPerformed

	void pnlLinksMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlLinksMouseDragged
		// Add your handling code here:

		Container container = pnlInScroll.getParent();
		if (container instanceof JViewport)
		{
			JViewport viewport = (JViewport) container;
			Point point = viewport.getViewPosition();
			int newX = point.x - (evt.getX() - downX);
			int newY = point.y - (evt.getY() - downY);

			int maxX = pnlInScroll.getWidth() - viewport.getWidth();
			int maxY = pnlInScroll.getHeight() - viewport.getHeight();

			if (newX < 0)
			{
				newX = 0;
			}
			if (newX > maxX)
			{
				newX = maxX;
			}
			if (newY < 0)
			{
				newY = 0;
			}
			if (newY > maxY)
			{
				newY = maxY;
			}

			viewport.setViewPosition(new Point(newX, newY));
		}
	}//GEN-LAST:event_pnlLinksMouseDragged

	void pnlLinksMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlLinksMouseReleased
		// Add your handling code here:
		pnlLinks.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}//GEN-LAST:event_pnlLinksMouseReleased

	void pnlLinksMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlLinksMousePressed
		// Add your handling code here:
		pnlLinks.setCursor(new Cursor(Cursor.MOVE_CURSOR));

		downX = evt.getX();
		downY = evt.getY();
	}//GEN-LAST:event_pnlLinksMousePressed

	void btnPrintActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPrintActionPerformed
	{//GEN-HEADEREND:event_btnPrintActionPerformed
		// Add your handling code here:

            SwingUtilities.invokeLater(
				new Runnable()
				{
					public void run()
					{
						try
						{
							
							btnPrint.setEnabled(false);
							JRViewer300.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							JasperPrintManager.printReport(jasperPrint, true);
						}
						catch (Exception ex)
						{
							ex.printStackTrace();
							JOptionPane.showMessageDialog(JRViewer300.this, getBundleString("error.printing"));
						}
						finally
						{
							JRViewer300.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							btnPrint.setEnabled(true);
						}
					}
				}
			);


	}//GEN-LAST:event_btnPrintActionPerformed

	void btnLastActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnLastActionPerformed
	{//GEN-HEADEREND:event_btnLastActionPerformed
		// Add your handling code here:
		setPageIndex(jasperPrint.getPages().size() - 1);
		refreshPage();
	}//GEN-LAST:event_btnLastActionPerformed

	void btnNextActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnNextActionPerformed
	{//GEN-HEADEREND:event_btnNextActionPerformed
		// Add your handling code here:
		setPageIndex(pageIndex + 1);
		refreshPage();
	}//GEN-LAST:event_btnNextActionPerformed

	void btnPreviousActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPreviousActionPerformed
	{//GEN-HEADEREND:event_btnPreviousActionPerformed
		// Add your handling code here:
		setPageIndex(pageIndex - 1);
		refreshPage();
	}//GEN-LAST:event_btnPreviousActionPerformed

	void btnFirstActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnFirstActionPerformed
	{//GEN-HEADEREND:event_btnFirstActionPerformed
		// Add your handling code here:
		setPageIndex(0);
		refreshPage();
	}//GEN-LAST:event_btnFirstActionPerformed

	void btnReloadActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnReloadActionPerformed
	{//GEN-HEADEREND:event_btnReloadActionPerformed
		// Add your handling code here:
		if (type == TYPE_FILE_NAME)
		{
			try
			{
				loadReport(reportFileName, isXML);
			}
			catch (JRException e)
			{
				e.printStackTrace();

				jasperPrint = null;
				setPageIndex(0);
				refreshPage();

				JOptionPane.showMessageDialog(this, getBundleString("error.loading"));
			}

			forceRefresh();
		}
	}//GEN-LAST:event_btnReloadActionPerformed

	protected void forceRefresh()
	{
		zoom = 0;//force pageRefresh()
		realZoom = 0f;
		setZoomRatio(1);
	}

	void btnZoomInActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnZoomInActionPerformed
	{//GEN-HEADEREND:event_btnZoomInActionPerformed
		// Add your handling code here:
		btnActualSize.setSelected(false);
		btnFitPage.setSelected(false);
		btnFitWidth.setSelected(false);

		int newZoomInt = (int)(100 * getZoomRatio());
		int index = Arrays.binarySearch(zooms, newZoomInt);
		if (index < 0)
		{
			setZoomRatio(zooms[- index - 1] / 100f);
		}
		else if (index < cmbZoom.getModel().getSize() - 1)
		{
			setZoomRatio(zooms[index + 1] / 100f);
		}
	}//GEN-LAST:event_btnZoomInActionPerformed

	void btnZoomOutActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnZoomOutActionPerformed
	{//GEN-HEADEREND:event_btnZoomOutActionPerformed
		// Add your handling code here:
		btnActualSize.setSelected(false);
		btnFitPage.setSelected(false);
		btnFitWidth.setSelected(false);

		int newZoomInt = (int)(100 * getZoomRatio());
		int index = Arrays.binarySearch(zooms, newZoomInt);
		if (index > 0)
		{
			setZoomRatio(zooms[index - 1] / 100f);
		}
		else if (index < -1)
		{
			setZoomRatio(zooms[- index - 2] / 100f);
		}
	}//GEN-LAST:event_btnZoomOutActionPerformed

	void cmbZoomActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cmbZoomActionPerformed
	{//GEN-HEADEREND:event_cmbZoomActionPerformed
		// Add your handling code here:
		float newZoom = getZoomRatio();

		if (newZoom < MIN_ZOOM)
		{
			newZoom = MIN_ZOOM;
		}

		if (newZoom > MAX_ZOOM)
		{
			newZoom = MAX_ZOOM;
		}

		setZoomRatio(newZoom);
	}//GEN-LAST:event_cmbZoomActionPerformed


	/**
	*/
	void hyperlinkClicked(MouseEvent evt)
	{
		JPanel link = (JPanel)evt.getSource();
		JRPrintHyperlink element = (JRPrintHyperlink)linksMap.get(link);
		hyperlinkClicked(element);
	}


	protected void hyperlinkClicked(JRPrintHyperlink hyperlink)
	{
		try
		{
			JRHyperlinkListener listener = null;
			for(int i = 0; i < hyperlinkListeners.size(); i++)
			{
				listener = (JRHyperlinkListener)hyperlinkListeners.get(i);
				listener.gotoHyperlink(hyperlink);
			}
		}
		catch(JRException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, getBundleString("error.hyperlink"));
		}
	}


	/**
	*/
	public int getPageIndex()
	{
		return pageIndex;
	}


	/**
	*/
	private void setPageIndex(int index)
	{
		if (
			jasperPrint != null &&
			jasperPrint.getPages() != null &&
			jasperPrint.getPages().size() > 0
			)
		{
			if (index >= 0 && index < jasperPrint.getPages().size())
			{
				pageIndex = index;
				pageError = false;
				btnFirst.setEnabled( (pageIndex > 0) );
				btnPrevious.setEnabled( (pageIndex > 0) );
				btnNext.setEnabled( (pageIndex < jasperPrint.getPages().size() - 1) );
				btnLast.setEnabled( (pageIndex < jasperPrint.getPages().size() - 1) );
				txtGoTo.setEnabled(btnFirst.isEnabled() || btnLast.isEnabled());
				txtGoTo.setText("" + (pageIndex + 1));
				lblStatus.setText(
					MessageFormat.format(
						getBundleString("page"),
						new Object[]{new Integer(pageIndex + 1), new Integer(jasperPrint.getPages().size())}
						)
					);
			}
		}
		else
		{
			btnFirst.setEnabled(false);
			btnPrevious.setEnabled(false);
			btnNext.setEnabled(false);
			btnLast.setEnabled(false);
			txtGoTo.setEnabled(false);
			txtGoTo.setText("");
			lblStatus.setText("");
		}
	}


	/**
	*/
	protected void loadReport(String fileName, boolean isXmlReport) throws JRException
	{
		if (isXmlReport)
		{
			jasperPrint = JRPrintXmlLoader.load(fileName);
		}
		else
		{
			jasperPrint = (JasperPrint)JRLoader.loadObject(fileName);
		}

		type = TYPE_FILE_NAME;
		this.isXML = isXmlReport;
		reportFileName = fileName;
		btnReload.setEnabled(true);
		setPageIndex(0);
	}


	/**
	*/
	protected void loadReport(InputStream is, boolean isXmlReport) throws JRException
	{
		if (isXmlReport)
		{
			jasperPrint = JRPrintXmlLoader.load(is);
		}
		else
		{
			jasperPrint = (JasperPrint)JRLoader.loadObject(is);
		}

		type = TYPE_INPUT_STREAM;
		this.isXML = isXmlReport;
		btnReload.setEnabled(false);
		setPageIndex(0);
	}


	/**
	*/
	protected void loadReport(JasperPrint jrPrint)
	{
		jasperPrint = jrPrint;
		type = TYPE_OBJECT;
		isXML = false;
		btnReload.setEnabled(false);
		setPageIndex(0);
	}

	/**
	*/
	protected void refreshPage()
	{
		if (
			jasperPrint == null ||
			jasperPrint.getPages() == null ||
			jasperPrint.getPages().size() == 0
			)
		{
			pnlPage.setVisible(false);
			btnSave.setEnabled(false);
			btnPrint.setEnabled(false);
			btnActualSize.setEnabled(false);
			btnFitPage.setEnabled(false);
			btnFitWidth.setEnabled(false);
			btnZoomIn.setEnabled(false);
			btnZoomOut.setEnabled(false);
			cmbZoom.setEnabled(false);

			if (jasperPrint != null)
			{
				JOptionPane.showMessageDialog(this, getBundleString("no.pages"));
			}

			return;
		}

		pnlPage.setVisible(true);
		btnSave.setEnabled(true);
		btnPrint.setEnabled(true);
		btnActualSize.setEnabled(true);
		btnFitPage.setEnabled(true);
		btnFitWidth.setEnabled(true);
		btnZoomIn.setEnabled(zoom < MAX_ZOOM);
		btnZoomOut.setEnabled(zoom > MIN_ZOOM);
		cmbZoom.setEnabled(true);

		Dimension dim = new Dimension(
			(int)(jasperPrint.getPageWidth() * realZoom) + 8, // 2 from border, 5 from shadow and 1 extra pixel for image
			(int)(jasperPrint.getPageHeight() * realZoom) + 8
			);
		pnlPage.setMaximumSize(dim);
		pnlPage.setMinimumSize(dim);
		pnlPage.setPreferredSize(dim);

		long maxImageSize = JRProperties.getLongProperty(VIEWER_RENDER_BUFFER_MAX_SIZE);
		boolean renderImage;
		if (maxImageSize <= 0)
		{
			renderImage = false;
		}
		else
		{
			long imageSize = JRPrinterAWT.getImageSize(jasperPrint, realZoom);
			renderImage = imageSize <= maxImageSize;
		}

		lblPage.setRenderImage(renderImage);

		if (renderImage)
		{
			setPageImage();
		}

		pnlLinks.removeAll();
		linksMap = new HashMap();

		createHyperlinks();

		if (!renderImage)
		{
			lblPage.setIcon(null);

			pnlMain.validate();
			pnlMain.repaint();
		}
	}


	protected void setPageImage()
	{
		Image image;
		if (pageError)
		{
			image = getPageErrorImage();
		}
		else
		{
			try
			{
				image = JasperPrintManager.printPageToImage(jasperPrint, pageIndex, realZoom);
			}
			catch (Exception e)
			{
				pageError = true;
				e.printStackTrace();

				image = getPageErrorImage();
				JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("net/sf/jasperreports/view/viewer").getString("error.displaying"));
			}
		}
		ImageIcon imageIcon = new ImageIcon(image);
		lblPage.setIcon(imageIcon);
	}

	protected Image getPageErrorImage()
	{
		Image image = new BufferedImage(
				(int) (jasperPrint.getPageWidth() * realZoom) + 1,
				(int) (jasperPrint.getPageHeight() * realZoom) + 1,
				BufferedImage.TYPE_INT_RGB
				);
		
		Graphics2D grx = (Graphics2D) image.getGraphics();
		AffineTransform transform = new AffineTransform();
		transform.scale(realZoom, realZoom);
		grx.transform(transform);

		drawPageError((Graphics2D) grx);
		
		return image;
	}

	protected void createHyperlinks()
	{
		java.util.List pages = jasperPrint.getPages();
		JRPrintPage page = (JRPrintPage)pages.get(pageIndex);
		createHyperlinks(page.getElements(), 0, 0);
	}

	protected void createHyperlinks(List elements, int offsetX, int offsetY)
	{
		if(elements != null && elements.size() > 0)
		{
			for(Iterator it = elements.iterator(); it.hasNext();)
			{
				JRPrintElement element = (JRPrintElement)it.next();

				JRImageMapRenderer imageMap = null;
				if (element instanceof JRPrintImage)
				{
					JRRenderable renderer = ((JRPrintImage) element).getRenderer();
					if (renderer instanceof JRImageMapRenderer)
					{
						imageMap = (JRImageMapRenderer) renderer;
						if (!imageMap.hasImageAreaHyperlinks())
						{
							imageMap = null;
						}
					}
				}
				boolean hasImageMap = imageMap != null;

				JRPrintHyperlink hyperlink = null;
				if (element instanceof JRPrintHyperlink)
				{
					hyperlink = (JRPrintHyperlink) element;
				}
				boolean hasHyperlink = !hasImageMap 
					&& hyperlink != null && hyperlink.getHyperlinkType() != JRHyperlink.HYPERLINK_TYPE_NONE;
				boolean hasTooltip = hyperlink != null && hyperlink.getHyperlinkTooltip() != null;

				if (hasHyperlink || hasImageMap || hasTooltip)
				{
					JPanel link;
					if (hasImageMap)
					{
						Rectangle renderingArea = new Rectangle(0, 0, element.getWidth(), element.getHeight());
						link = new ImageMapPanel(renderingArea, imageMap);
					}
					else //hasImageMap
					{
						link = new JPanel();
						if (hasHyperlink)
						{
							link.addMouseListener(mouseListener);
						}
					}

					if (hasHyperlink)
					{
						link.setCursor(new Cursor(Cursor.HAND_CURSOR));
					}

					link.setLocation(
						(int)((element.getX() + offsetX) * realZoom),
						(int)((element.getY() + offsetY) * realZoom)
						);
					link.setSize(
						(int)(element.getWidth() * realZoom),
						(int)(element.getHeight() * realZoom)
						);
					link.setOpaque(false);

					String toolTip = getHyperlinkTooltip(hyperlink);
					if (toolTip == null && hasImageMap)
					{
						toolTip = "";//not null to register the panel as having a tool tip
					}
					link.setToolTipText(toolTip);

					pnlLinks.add(link);
					linksMap.put(link, element);
				}

				if (element instanceof JRPrintFrame)
				{
					JRPrintFrame frame = (JRPrintFrame) element;
					int frameOffsetX = offsetX + frame.getX() + frame.getLineBox().getLeftPadding().intValue();
					int frameOffsetY = offsetY + frame.getY() + frame.getLineBox().getTopPadding().intValue();
					createHyperlinks(frame.getElements(), frameOffsetX, frameOffsetY);
				}
			}
		}
	}


	protected class ImageMapPanel extends JPanel implements MouseListener, MouseMotionListener
	{
		private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

		protected final List imageAreaHyperlinks;

		public ImageMapPanel(Rectangle renderingArea, JRImageMapRenderer imageMap)
		{
			try
			{
				imageAreaHyperlinks = imageMap.getImageAreaHyperlinks(renderingArea);//FIXMECHART
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}

			addMouseListener(this);
			addMouseMotionListener(this);
		}

		public String getToolTipText(MouseEvent event)
		{
			String tooltip = null;
			JRPrintImageAreaHyperlink imageMapArea = getImageMapArea(event);
			if (imageMapArea != null)
			{
				tooltip = getHyperlinkTooltip(imageMapArea.getHyperlink());
			}

			if (tooltip == null)
			{
				tooltip = super.getToolTipText(event);
			}

			return tooltip;
		}

		public void mouseDragged(MouseEvent e)
		{
			pnlLinksMouseDragged(e);
		}

		public void mouseMoved(MouseEvent e)
		{
			JRPrintImageAreaHyperlink imageArea = getImageMapArea(e);
			if (imageArea != null
					&& imageArea.getHyperlink().getHyperlinkType() != JRHyperlink.HYPERLINK_TYPE_NONE)
			{
				e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			else
			{
				e.getComponent().setCursor(Cursor.getDefaultCursor());
			}
		}

		protected JRPrintImageAreaHyperlink getImageMapArea(MouseEvent e)
		{
			return getImageMapArea((int) (e.getX() / realZoom), (int) (e.getY() / realZoom));
		}

		protected JRPrintImageAreaHyperlink getImageMapArea(int x, int y)
		{
			JRPrintImageAreaHyperlink image = null;
			if (imageAreaHyperlinks != null)
			{
				for (ListIterator it = imageAreaHyperlinks.listIterator(imageAreaHyperlinks.size()); image == null && it.hasPrevious();)
				{
					JRPrintImageAreaHyperlink area = (JRPrintImageAreaHyperlink) it.previous();
					if (area.getArea().containsPoint(x, y))
					{
						image = area;
					}
				}
			}
			return image;
		}

		public void mouseClicked(MouseEvent e)
		{
			JRPrintImageAreaHyperlink imageMapArea = getImageMapArea(e);
			if (imageMapArea != null)
			{
				hyperlinkClicked(imageMapArea.getHyperlink());
			}
		}

		public void mouseEntered(MouseEvent e)
		{
		}

		public void mouseExited(MouseEvent e)
		{
		}

		public void mousePressed(MouseEvent e)
		{
			e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			pnlLinksMousePressed(e);
		}

		public void mouseReleased(MouseEvent e)
		{
			e.getComponent().setCursor(Cursor.getDefaultCursor());
			pnlLinksMouseReleased(e);
		}
	}


	protected String getHyperlinkTooltip(JRPrintHyperlink hyperlink)
	{
		String toolTip;
		toolTip = hyperlink.getHyperlinkTooltip();
		if (toolTip == null)
		{
			toolTip = getFallbackTooltip(hyperlink);
		}
		return toolTip;
	}


	protected String getFallbackTooltip(JRPrintHyperlink hyperlink)
	{
		String toolTip = null;
		switch(hyperlink.getHyperlinkType())
		{
			case JRHyperlink.HYPERLINK_TYPE_REFERENCE :
			{
				toolTip = hyperlink.getHyperlinkReference();
				break;
			}
			case JRHyperlink.HYPERLINK_TYPE_LOCAL_ANCHOR :
			{
				if (hyperlink.getHyperlinkAnchor() != null)
				{
					toolTip = "#" + hyperlink.getHyperlinkAnchor();
				}
				break;
			}
			case JRHyperlink.HYPERLINK_TYPE_LOCAL_PAGE :
			{
				if (hyperlink.getHyperlinkPage() != null)
				{
					toolTip = "#page " + hyperlink.getHyperlinkPage();
				}
				break;
			}
			case JRHyperlink.HYPERLINK_TYPE_REMOTE_ANCHOR :
			{
				toolTip = "";
				if (hyperlink.getHyperlinkReference() != null)
				{
					toolTip = toolTip + hyperlink.getHyperlinkReference();
				}
				if (hyperlink.getHyperlinkAnchor() != null)
				{
					toolTip = toolTip + "#" + hyperlink.getHyperlinkAnchor();
				}
				break;
			}
			case JRHyperlink.HYPERLINK_TYPE_REMOTE_PAGE :
			{
				toolTip = "";
				if (hyperlink.getHyperlinkReference() != null)
				{
					toolTip = toolTip + hyperlink.getHyperlinkReference();
				}
				if (hyperlink.getHyperlinkPage() != null)
				{
					toolTip = toolTip + "#page " + hyperlink.getHyperlinkPage();
				}
				break;
			}
			default :
			{
				break;
			}
		}
		return toolTip;
	}


	/**
	*/
	private void emptyContainer(Container container)
	{
		Component[] components = container.getComponents();

		if (components != null)
		{
			for(int i = 0; i < components.length; i++)
			{
				if (components[i] instanceof Container)
				{
					emptyContainer((Container)components[i]);
				}
			}
		}

		components = null;
		container.removeAll();
		container = null;
	}


	/**
	*/
	private float getZoomRatio()
	{
		float newZoom = zoom;

		try
		{
			newZoom =
				zoomDecimalFormat.parse(
					String.valueOf(cmbZoom.getEditor().getItem())
					).floatValue() / 100f;
		}
		catch(ParseException e)
		{
		}

		return newZoom;
	}


	/**
	*/
	public void setZoomRatio(float newZoom)
	{
		if (newZoom > 0)
		{
			cmbZoom.getEditor().setItem(
				zoomDecimalFormat.format(newZoom * 100) + "%"
				);

			if (zoom != newZoom)
			{
				zoom = newZoom;
				realZoom = zoom * screenResolution / REPORT_RESOLUTION;

				refreshPage();
			}
		}
	}


	/**
	*/
	private void setRealZoomRatio(float newZoom)
	{
		if (newZoom > 0 && realZoom != newZoom)
		{
			zoom = newZoom * REPORT_RESOLUTION / screenResolution;
			realZoom = newZoom;

			cmbZoom.getEditor().setItem(
				zoomDecimalFormat.format(zoom * 100) + "%"
				);

			refreshPage();
		}
	}


	/**
	 *
	 */
	public void setFitWidthZoomRatio()
	{
		setRealZoomRatio(((float)pnlInScroll.getVisibleRect().getWidth() - 20f) / jasperPrint.getPageWidth());

	}

	public void setFitPageZoomRatio()
	{
		setRealZoomRatio(((float)pnlInScroll.getVisibleRect().getHeight() - 20f) / jasperPrint.getPageHeight());
	}


	/**
	 * 
	 */
	protected JRGraphics2DExporter getGraphics2DExporter() throws JRException
	{
		return new JRGraphics2DExporter();
	}

	/**
	 * 
	 */
	protected void paintPage(Graphics2D grx)
	{
		if (pageError)
		{
			paintPageError(grx);
			return;
		}
		
		try
		{
			if (exporter == null)
			{
				exporter = getGraphics2DExporter();
			}
			else
			{
				exporter.reset();
			}

			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRGraphics2DExporterParameter.GRAPHICS_2D, grx.create());
			exporter.setParameter(JRExporterParameter.PAGE_INDEX, new Integer(pageIndex));
			exporter.setParameter(JRGraphics2DExporterParameter.ZOOM_RATIO, new Float(realZoom));
			exporter.setParameter(JRExporterParameter.OFFSET_X, new Integer(1)); //lblPage border
			exporter.setParameter(JRExporterParameter.OFFSET_Y, new Integer(1));
			exporter.exportReport();
		}
		catch(Exception e)
		{
			pageError = true;
			e.printStackTrace();
			
			paintPageError(grx);
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					JOptionPane.showMessageDialog(JRViewer300.this, getBundleString("error.displaying"));
				}
			});
		}

	}

	protected void paintPageError(Graphics2D grx)
	{
		AffineTransform origTransform = grx.getTransform();
		
		AffineTransform transform = new AffineTransform();
		transform.translate(1, 1);
		transform.scale(realZoom, realZoom);
		grx.transform(transform);
		
		try
		{
			drawPageError(grx);
		}
		finally
		{
			grx.setTransform(origTransform);
		}
	}

	protected void drawPageError(Graphics grx)
	{
		grx.setColor(Color.white);
		grx.fillRect(0, 0, jasperPrint.getPageWidth() + 1, jasperPrint.getPageHeight() + 1);
	}

	protected void keyNavigate(KeyEvent evt)
	{
		boolean refresh = true;
		switch (evt.getKeyCode())
		{
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_PAGE_DOWN:
			dnNavigate(evt);
			break;
		case KeyEvent.VK_UP:
		case KeyEvent.VK_PAGE_UP:
			upNavigate(evt);
			break;
		case KeyEvent.VK_HOME:
			homeEndNavigate(0);
			break;
		case KeyEvent.VK_END:
			homeEndNavigate(jasperPrint.getPages().size() - 1);
			break;
		default:
			refresh = false;
		}
		
		if (refresh)
		{
			refreshPage();
		}
	}

	private void dnNavigate(KeyEvent evt)
	{
		int bottomPosition = scrollPane.getVerticalScrollBar().getValue();
		scrollPane.dispatchEvent(evt);
		if((scrollPane.getViewport().getHeight() > pnlPage.getHeight() ||
				scrollPane.getVerticalScrollBar().getValue() == bottomPosition) &&
				pageIndex < jasperPrint.getPages().size() - 1)
		{
			setPageIndex(pageIndex + 1);
			if(scrollPane.isEnabled())
				scrollPane.getVerticalScrollBar().setValue(0);
		}
	}

	private void upNavigate(KeyEvent evt)
	{
		if((scrollPane.getViewport().getHeight() > pnlPage.getHeight() ||
				scrollPane.getVerticalScrollBar().getValue() == 0) &&
				pageIndex > 0)
		{
			setPageIndex(pageIndex - 1);
			if(scrollPane.isEnabled())
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		}
		else
		{
			scrollPane.dispatchEvent(evt);
		}
	}

	private void homeEndNavigate(int pageNumber)
	{
		setPageIndex(pageNumber);
		if(scrollPane.isEnabled())
			scrollPane.getVerticalScrollBar().setValue(0);
	}

	/**
	 *
	*/
	private void fitPage(){
		float heightRatio = ((float)pnlInScroll.getVisibleRect().getHeight() - 20f) / jasperPrint.getPageHeight();
		float widthRatio = ((float)pnlInScroll.getVisibleRect().getWidth() - 20f) / jasperPrint.getPageWidth();
		setRealZoomRatio(heightRatio < widthRatio ? heightRatio : widthRatio);
	}

	/**
	*/
	class PageRenderer extends JLabel
	{
		private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

		private boolean renderImage;
		JRViewer300 viewer = null;

		public PageRenderer(JRViewer300 viewer)
		{
			this.viewer = viewer;
		}

		public void paintComponent(Graphics g)
		{
			if (isRenderImage())
			{
				super.paintComponent(g);
			}
			else
			{
				viewer.paintPage((Graphics2D)g.create());
			}
		}

		public boolean isRenderImage()
		{
			return renderImage;
		}

		public void setRenderImage(boolean renderImage)
		{
			this.renderImage = renderImage;
		}
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JToggleButton btnActualSize;
    protected javax.swing.JButton btnFirst;
    protected javax.swing.JToggleButton btnFitPage;
    protected javax.swing.JToggleButton btnFitWidth;
    protected javax.swing.JButton btnLast;
    protected javax.swing.JButton btnNext;
    protected javax.swing.JButton btnPrevious;
    protected javax.swing.JButton btnPrint;
    protected javax.swing.JButton btnReload;
    protected javax.swing.JButton btnSave;
    protected javax.swing.JButton btnZoomIn;
    protected javax.swing.JButton btnZoomOut;
    protected javax.swing.JComboBox cmbZoom;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar jToolBar1;
    private PageRenderer lblPage;
    protected javax.swing.JLabel lblStatus;
    private javax.swing.JPanel pnlInScroll;
    private javax.swing.JPanel pnlLinks;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlPage;
    private javax.swing.JScrollPane scrollPane;
    protected javax.swing.JTextField txtGoTo;
    // End of variables declaration//GEN-END:variables

}
