package org.gvt.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.events.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.resource.ImageDescriptor;
import org.gvt.ChisioMain;
import org.gvt.action.*;
import org.ivis.layout.LayoutConstants;
import org.ivis.layout.LayoutOptionsPack;
import org.ivis.layout.avsdf.AVSDFConstants;
import org.ivis.layout.cise.CiSEConstants;
import org.ivis.layout.cose.CoSEConstants;
import org.ivis.layout.fd.FDLayoutConstants;
import org.ivis.layout.sgym.SgymConstants;
import org.ivis.layout.spring.SpringConstants;

/**
 * This class maintains the Layout Properties dialog to set the parameters of
 * each layout
 *
 * @author Cihan Kucukkececi
 *
 * Copyright: I-Vis Research Group, Bilkent University, 2007
 */
public class LayoutInspector extends Dialog
{
	//General
	private Button incremental;
	private Button proofButton;
	private Button defaultButton;
	private Button draftButton;
	private Group tuningGroup;
	private Scale animationPeriod;
	protected Button animateDuringLayoutButton;
	protected Button animateOnLayoutButton;
	private Button createBendsAsButton;
	private Button uniformLeafNodeSizesButton;

	//CoSE
	private Scale gravityStrength,
		gravityRange,
		springStrength,
		repulsionStrength,
		compoundGravityStrength,
		compoundGravityRange;
	private Text desiredEdgeLengthCoSE;
	private Button smartEdgeLengthCalc;
	private Button multiLevelScaling;
	private Button smartRepulsionRangeCalc;

	//Spring
	private Text disconnectedNodeDistanceSpringRestLength;
	private Text nodeDistanceRestLength;

	protected Object result;

	protected Shell shell;

	private ChisioMain main;

	public static int lastTab = 0;

	private KeyAdapter keyAdapter = new KeyAdapter()
	{
		public void keyPressed(KeyEvent arg0)
		{
			arg0.doit = isDigit(arg0.keyCode);
		}

		public boolean isDigit(int keyCode)
		{
			if (Character.isDigit(keyCode)
				|| keyCode == SWT.DEL
				|| keyCode == 8
				|| keyCode == SWT.ARROW_LEFT
				|| keyCode == SWT.ARROW_RIGHT)
			{
				return true;
			}
			return false;
		}
	};

	public static void main(String args[])
	{
		//	new LayoutInspector(new Shell(), SWT.TITLE).open();
	}

	/**
	 * Create the dialog
	 */
	public LayoutInspector(ChisioMain main)
	{
		super(main.getShell(), SWT.NONE);
		this.main = main;
	}

	/**
	 * Open the dialog
	 */
	public Object open()
	{
		createContents();
		shell.pack();

		shell.setLocation(
			getParent().getLocation().x + (getParent().getSize().x / 2) -
				(shell.getSize().x / 2),
			getParent().getLocation().y + (getParent().getSize().y / 2) -
				(shell.getSize().y / 2));

		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}

	/**
	 * Create contents of the dialog
	 */
	protected void createContents()
	{
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText("Layout Properties");

		ImageDescriptor id = ImageDescriptor.createFromFile(ChisioMain.class, "icon/cbe-icon.png");
		shell.setImage(id.createImage());

		GridLayout gridLy = new GridLayout();
		gridLy.numColumns = 1;
		shell.setLayout(gridLy);

		final TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		GridData gridDt = new GridData();
		gridDt.grabExcessVerticalSpace = true;
		tabFolder.setLayoutData(gridDt);

		final TabItem generalTabItem = new TabItem(tabFolder, SWT.NONE);
		generalTabItem.setText("General");

		final Composite compositeGeneral = new Composite(tabFolder, SWT.NONE);
		generalTabItem.setControl(compositeGeneral);

		gridLy = new GridLayout();
		gridLy.numColumns = 2;
		compositeGeneral.setLayout(gridLy);

		final Group animationGroup = new Group(compositeGeneral, SWT.NONE);
		animationGroup.setText("Animation");
		animationGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL));
		gridLy = new GridLayout();
		gridLy.numColumns = 1;
		animationGroup.setLayout(gridLy);

		animateOnLayoutButton = new Button(animationGroup, SWT.CHECK);
		animateOnLayoutButton.setText("Animate on Layout");

		animateDuringLayoutButton = new Button(animationGroup, SWT.CHECK);
		animateDuringLayoutButton.setAlignment(SWT.UP);
		animateDuringLayoutButton.setText("Animate during Layout");

		Composite periodComposite = new Composite(animationGroup, SWT.NONE);
		gridLy = new GridLayout();
		gridLy.numColumns = 2;
		periodComposite.setLayout(gridLy);

		final Label animationPeriodLabel = new Label(periodComposite, SWT.NONE);
		animationPeriodLabel.setText("Animation Period");
		gridDt = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		animationPeriodLabel.setLayoutData(gridDt);

		animationPeriod = new Scale(periodComposite, SWT.NONE);
		animationPeriod.setSelection(50);
		animationPeriod.setIncrement(5);
		gridDt = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		gridDt.widthHint = 50;
		animationPeriod.setLayoutData(gridDt);

		final Group layoutQualityGroup = new Group(compositeGeneral, SWT.NONE);
		layoutQualityGroup.setText("Layout Quality");
		gridLy = new GridLayout();
		gridLy.numColumns = 1;
		layoutQualityGroup.setLayout(gridLy);

		draftButton = new Button(layoutQualityGroup, SWT.RADIO);
		draftButton.setText("Draft");

		defaultButton = new Button(layoutQualityGroup, SWT.RADIO);
		defaultButton.setText("Default");

		proofButton = new Button(layoutQualityGroup, SWT.RADIO);
		proofButton.setText("Proof");

		Composite extra = new Composite(compositeGeneral, SWT.NONE);
		gridDt = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridDt.horizontalSpan = 2;
		extra.setLayoutData(gridDt);
		gridLy = new GridLayout();
		gridLy.numColumns = 1;
		extra.setLayout(gridLy);

		incremental = new Button(extra, SWT.CHECK);
		incremental.setText("Incremental");

		createBendsAsButton = new Button(extra, SWT.CHECK);
		createBendsAsButton.setText("Create Bends as Needed");

		uniformLeafNodeSizesButton = new Button(extra, SWT.CHECK);
		uniformLeafNodeSizesButton.setText("Uniform Leaf Node Sizes");

		final TabItem coseTabItem = new TabItem(tabFolder, SWT.NONE);
		coseTabItem.setText("CoSE");

		final TabItem springTabItem = new TabItem(tabFolder, SWT.NONE);
		springTabItem.setText("Spring");

		final Composite compositeCoSE = new Composite(tabFolder, SWT.NONE);
		coseTabItem.setControl(compositeCoSE);
		gridLy = new GridLayout();
		gridLy.numColumns = 1;
		compositeCoSE.setLayout(gridLy);

		//--- Tuning group ------------------------------------------------------------------------|

		tuningGroup = new Group(compositeCoSE, SWT.NONE);
		tuningGroup.setText("Tuning");
		gridLy = new GridLayout();
		gridLy.numColumns = 2;
		tuningGroup.setLayout(gridLy);

		final Label springStrengthLabel = new Label(tuningGroup, SWT.NONE);
		springStrengthLabel.setText("Spring Strength");

		springStrength = new Scale(tuningGroup, SWT.NONE);
		springStrength.setIncrement(5);
		gridDt = new GridData();
		gridDt.widthHint = 100;
		springStrength.setLayoutData(gridDt);

		final Label repulsionStrengthLabel = new Label(tuningGroup, SWT.NONE);
		repulsionStrengthLabel.setText("Repulsion");

		repulsionStrength = new Scale(tuningGroup, SWT.NONE);
		repulsionStrength.setIncrement(5);
		repulsionStrength.setLayoutData(gridDt);

		final Label gravityLevelLabel = new Label(tuningGroup, SWT.NONE);
		gravityLevelLabel.setText("Gravity");

		gravityStrength = new Scale(tuningGroup, SWT.NONE);
		gravityStrength.setIncrement(5);
		gravityStrength.setLayoutData(gridDt);

		final Label gravityRangeLabel = new Label(tuningGroup, SWT.NONE);
		gravityRangeLabel.setText("Gravity Range");

		gravityRange = new Scale(tuningGroup, SWT.NONE);
		gravityRange.setIncrement(5);
		gravityRange.setLayoutData(gridDt);

		final Label compoundGravityStrengthLabel = new Label(tuningGroup, SWT.NONE);
		compoundGravityStrengthLabel.setText("Compound Gravity");

		compoundGravityStrength = new Scale(tuningGroup, SWT.NONE);
		compoundGravityStrength.setIncrement(5);
		compoundGravityStrength.setLayoutData(gridDt);
		compositeCoSE.setTabList(new Control[] {tuningGroup});

		final Label compoundGravityRangeLabel = new Label(tuningGroup, SWT.NONE);
		compoundGravityRangeLabel.setText("Compound Gravity Range");

		compoundGravityRange = new Scale(tuningGroup, SWT.NONE);
		compoundGravityRange.setIncrement(5);
		compoundGravityRange.setLayoutData(gridDt);

		smartRepulsionRangeCalc = new Button(tuningGroup, SWT.CHECK);
		smartRepulsionRangeCalc.setText("Smart Range Calculation");

		//--- End of tuning group -----------------------------------------------------------------|

		Composite desire = new Composite(compositeCoSE, SWT.NONE);
		gridLy = new GridLayout();
		gridLy.numColumns = 2;
		desire.setLayout(gridLy);

		final Label desiredEdgeLengthLabel = new Label(desire, SWT.NONE);
		desiredEdgeLengthLabel.setText("Desired Edge Length    ");
		gridDt = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		desiredEdgeLengthLabel.setLayoutData(gridDt);

		desiredEdgeLengthCoSE = new Text(desire, SWT.BORDER);
		desiredEdgeLengthCoSE.addKeyListener(keyAdapter);
		gridDt = new GridData();
		gridDt.widthHint = 30;
		desiredEdgeLengthCoSE.setLayoutData(gridDt);

		smartEdgeLengthCalc = new Button(compositeCoSE, SWT.CHECK);
		smartEdgeLengthCalc.setText("Smart Edge Length Calculation");

		multiLevelScaling = new Button(compositeCoSE, SWT.CHECK);
		multiLevelScaling.setText("Multi-Level Scaling");

		compositeCoSE.setTabList(new Control[] {tuningGroup});

		final Composite compositeSpring = new Composite(tabFolder, SWT.NONE);
		springTabItem.setControl(compositeSpring);
		gridLy = new GridLayout();
		gridLy.numColumns = 2;
		compositeSpring.setLayout(gridLy);

		final Label nodedistancerestlengthLabel = new Label(compositeSpring, SWT.NONE);
		nodedistancerestlengthLabel.setText("Desired Edge Length");

		nodeDistanceRestLength = new Text(compositeSpring, SWT.BORDER);
		nodeDistanceRestLength.addKeyListener(keyAdapter);
		gridDt = new GridData();
		gridDt.widthHint = 30;
		nodeDistanceRestLength.setLayoutData(gridDt);

		final Label disconnectednodedistancespringrestlengthLabel =
			new Label(compositeSpring, SWT.NONE);
		disconnectednodedistancespringrestlengthLabel.
			setText("Disconnected Component Separation");

		disconnectedNodeDistanceSpringRestLength = new Text(compositeSpring, SWT.BORDER);
		disconnectedNodeDistanceSpringRestLength.addKeyListener(keyAdapter);
		gridDt = new GridData();
		gridDt.widthHint = 30;
		disconnectedNodeDistanceSpringRestLength.setLayoutData(gridDt);

		Composite buttons = new Composite(shell, SWT.NONE);
		buttons.setLayout(new RowLayout());
		gridDt = new GridData();
		gridDt.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;

		final Button okButton = new Button(buttons, SWT.NONE);
		okButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				storeValuesToOptionsPack();
				lastTab = tabFolder.getSelectionIndex();
				shell.close();
			}
		});
		okButton.setText("OK");

		final Button layoutButton = new Button(buttons, SWT.NONE);
		layoutButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				int i = tabFolder.getSelectionIndex();
				storeValuesToOptionsPack();

				switch (i)
				{
					case 1:
						new CoSELayoutAction(main).run();
						break;
					case 2:
						new SpringLayoutAction(main).run();
						break;
				}
			}
		});

		layoutButton.setText("Layout");

		final Button cancelButton = new Button(buttons, SWT.NONE);
		cancelButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				lastTab = tabFolder.getSelectionIndex();
				shell.close();
			}
		});

		cancelButton.setText("Cancel");

		final Button defaultButton2 = new Button(buttons, SWT.NONE);
		defaultButton2.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				int i = tabFolder.getSelectionIndex();
				setDefaultLayoutProperties(i);
			}
		});

		defaultButton2.setText("Default");

		if (lastTab == 0)
		{
			layoutButton.setEnabled(false);
		}

		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0)
			{
				if (tabFolder.getSelectionIndex() == 0)
				{
					layoutButton.setEnabled(false);
				}
				else
				{
					layoutButton.setEnabled(true);
				}
			}
		});

		setInitialValues();

		tabFolder.setSelection(lastTab);
	}

	public void storeValuesToOptionsPack()
	{
		LayoutOptionsPack lop = LayoutOptionsPack.getInstance();

		//General
		if (proofButton.getSelection())
		{
			lop.getGeneral().layoutQuality = LayoutConstants.PROOF_QUALITY;
		}
		else if (draftButton.getSelection())
		{
			lop.getGeneral().layoutQuality = LayoutConstants.DRAFT_QUALITY;
		}
		else
		{
			lop.getGeneral().layoutQuality = LayoutConstants.DEFAULT_QUALITY;
		}
		lop.getGeneral().animationPeriod = animationPeriod.getSelection();
		lop.getGeneral().animationDuringLayout = animateDuringLayoutButton.getSelection();
		lop.getGeneral().animationOnLayout = animateOnLayoutButton.getSelection();
		lop.getGeneral().incremental = incremental.getSelection();
		lop.getGeneral().createBendsAsNeeded = createBendsAsButton.getSelection();
		lop.getGeneral().uniformLeafNodeSizes = uniformLeafNodeSizesButton.getSelection();

		//CoSE
		lop.getCoSE().idealEdgeLength = Integer.parseInt(desiredEdgeLengthCoSE.getText());
		lop.getCoSE().springStrength = springStrength.getSelection();
		lop.getCoSE().repulsionStrength = repulsionStrength.getSelection();
		lop.getCoSE().smartRepulsionRangeCalc = smartRepulsionRangeCalc.getSelection();
		lop.getCoSE().gravityStrength = gravityStrength.getSelection();
		lop.getCoSE().gravityRange = gravityRange.getSelection();
		lop.getCoSE().compoundGravityStrength = compoundGravityStrength.getSelection();
		lop.getCoSE().compoundGravityRange = compoundGravityRange.getSelection();
		lop.getCoSE().smartEdgeLengthCalc = smartEdgeLengthCalc.getSelection();
		lop.getCoSE().multiLevelScaling = multiLevelScaling.getSelection();
	}

	public void setInitialValues()
	{
		LayoutOptionsPack lop = LayoutOptionsPack.getInstance();

		//General
		if (lop.getGeneral().layoutQuality == LayoutConstants.PROOF_QUALITY)
		{
			proofButton.setSelection(true);
		}
		else if (lop.getGeneral().layoutQuality == LayoutConstants.DRAFT_QUALITY)
		{
			draftButton.setSelection(true);
		}
		else
		{
			defaultButton.setSelection(true);
		}
		animateDuringLayoutButton.setSelection(lop.getGeneral().animationDuringLayout);
		animateOnLayoutButton.setSelection(lop.getGeneral().animationOnLayout);
		animationPeriod.setSelection(lop.getGeneral().animationPeriod);
		incremental.setSelection(lop.getGeneral().incremental);
		createBendsAsButton.setSelection(lop.getGeneral().createBendsAsNeeded);
		uniformLeafNodeSizesButton.setSelection(lop.getGeneral().uniformLeafNodeSizes);

		//CoSE
		desiredEdgeLengthCoSE.setText(String.valueOf(lop.getCoSE().idealEdgeLength));
		springStrength.setSelection(lop.getCoSE().springStrength);
		repulsionStrength.setSelection(lop.getCoSE().repulsionStrength);
		smartRepulsionRangeCalc.setSelection(lop.getCoSE().smartRepulsionRangeCalc);
		gravityStrength.setSelection(lop.getCoSE().gravityStrength);
		gravityRange.setSelection(lop.getCoSE().gravityRange);
		compoundGravityStrength.setSelection(lop.getCoSE().compoundGravityStrength);
		compoundGravityRange.setSelection(lop.getCoSE().compoundGravityRange);
		smartEdgeLengthCalc.setSelection(lop.getCoSE().smartEdgeLengthCalc);
		multiLevelScaling.setSelection(lop.getCoSE().multiLevelScaling);
	}

	public void setDefaultLayoutProperties(int select)
	{
		LayoutOptionsPack lop = LayoutOptionsPack.getInstance();

		if (select == 0)
		{
			//General
			if (lop.getGeneral().defaultLayoutQuality == LayoutConstants.PROOF_QUALITY)
			{
				proofButton.setSelection(true);
			}
			else if (lop.getGeneral().defaultLayoutQuality == LayoutConstants.DRAFT_QUALITY)
			{
				draftButton.setSelection(true);
			}
			else
			{
				defaultButton.setSelection(true);
			}
			animateDuringLayoutButton.setSelection(lop.getGeneral().defaultAnimationDuringLayout);
			animateOnLayoutButton.setSelection(lop.getGeneral().defaultAnimationOnLayout);
			animationPeriod.setSelection(lop.getGeneral().defaultAnimationPeriod);
			incremental.setSelection(lop.getGeneral().defaultIncremental);
			createBendsAsButton.setSelection(lop.getGeneral().defaultCreateBendsAsNeeded);
			uniformLeafNodeSizesButton.setSelection(lop.getGeneral().defaultUniformLeafNodeSizes);
		}
		else if (select == 1)
		{
			//CoSE
			desiredEdgeLengthCoSE.setText(String.valueOf(lop.getCoSE().defaultIdealEdgeLength));
			springStrength.setSelection(lop.getCoSE().defaultSpringStrength);
			repulsionStrength.setSelection(lop.getCoSE().defaultRepulsionStrength);
			smartRepulsionRangeCalc.setSelection(lop.getCoSE().defaultSmartRepulsionRangeCalc);
			gravityStrength.setSelection(lop.getCoSE().defaultGravityStrength);
			gravityRange.setSelection(lop.getCoSE().defaultGravityRange);
			compoundGravityStrength.setSelection(lop.getCoSE().defaultCompoundGravityStrength);
			compoundGravityRange.setSelection(lop.getCoSE().defaultCompoundGravityRange);
			smartEdgeLengthCalc.setSelection(lop.getCoSE().defaultSmartEdgeLengthCalc);
			multiLevelScaling.setSelection(lop.getCoSE().defaultMultiLevelScaling);
		}
	}
}