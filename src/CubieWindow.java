
//Rubik's Cube simulator

import java.awt.*;
import java.awt.event.*;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public final class CubieWindow extends JPanel
   implements ActionListener, ItemListener, FocusListener, Runnable {
   final int VIEWWIDTH = 300;

   final int VIEWHEIGHT = 350;

   int symType = 0;

   int viewer = 0;

   Viewer cubeViewers[] = { new Viewer3D(VIEWWIDTH, VIEWHEIGHT, this),
         new ViewerDiag(VIEWWIDTH, VIEWHEIGHT, this),
         new ViewerBox(VIEWWIDTH, VIEWHEIGHT, this),
         new ViewerFlat(VIEWWIDTH, VIEWHEIGHT, this) };

   Solver solvers[] = { new SolverKociemba(this), new SolverSquare(this),
         new SolverSlice(this), new SolverAntiSlice(this),
         new SolverTwoGen(this) };

   Panel rightPanel = new Panel();
   Button mixBut = new Button("Mix");
   Button resetBut = new Button("Reset");
   Button editBut = new Button("Edit");
   Button solveBut = new Button("Solve");
   Button viewBut = new Button("Change view");
   Button viewResetBut = new Button("Reset view");
   Panel boxPanel = new Panel();
   Button gensolBut = new Button("Solution");
   TextField textBox = new TextField("", 50);
   Button playBut = new ImButton(1);
   Button revBut = new ImButton(0);
   Button stepBut = new ImButton(5);
   Button backBut = new ImButton(4);
   Button endBut = new ImButton(7);
   Button beginBut = new ImButton(6);
   TabSet tabSet;
   Panel tabPanel[] = { new Panel(), new Panel() };
   Checkbox groupCheckBox[] = new Checkbox[solvers.length];
   Checkbox superBox = new Checkbox("supergroup");
   CheckboxGroup cubeGroup = new CheckboxGroup();
   Checkbox groupRadioBox[] = { new Checkbox("Normal", cubeGroup, true),
         new Checkbox("Square", cubeGroup, false),
         new Checkbox("Slice", cubeGroup, false),
         new Checkbox("Anti-Slice", cubeGroup, false),
         new Checkbox("2-generator", cubeGroup, false) };
   Checkbox symTwoColBox = new Checkbox("Two colors");
   SymButton symButton[] = new SymButton[29];
   SymButton symAllImage = new SymButton(null, 0);
   SymButton symCurrentImage = new SymButton(null, 0);
   Button symResetBut = new Button("Clear");
   Color colors[] = { new Color(255, 0, 0), // unprepared
         new Color(192, 192, 192), // prepared, ready, controls background color
         new Color(0, 255, 0), // Running solver
         new Color(160, 160, 160), // viewer background color
   };
   boolean solution = true;
   boolean symTwoCol = false;
   MoveSequence generator;
   int seqPos = 0;
   boolean playFw = true;
   boolean moveInProgress = false;

   // applet control
   boolean isPlaying = false;

   public void init() {
      int i, d, x, y;
      final int vw = VIEWWIDTH, vh = VIEWHEIGHT, gw = 220, bw = gw / 4, th = 50,
            bh = 20, bw2 = 75, bh2 = 16;
      Dimension dim = new Dimension(vw+gw, vh+th);
      setMinimumSize(dim);
      setMaximumSize(dim);
      setPreferredSize(dim);
      // build main applet panel
      setLayout(null);
      setBackground(colors[3]);
      add(rightPanel);
      add(boxPanel);
      for (i = 0; i < cubeViewers.length; i++) {
         add(cubeViewers[i]);
         cubeViewers[i].setBounds(0, 0, vw, vh);
         cubeViewers[i].setVisible(i == 0);
         cubeViewers[i].setBackground(colors[3]);
      }
      rightPanel.setBounds(vw, 0, gw, vh);
      boxPanel.setBounds(0, vh, vw + gw, th);

      // build right panel
      rightPanel.setLayout(null);
      rightPanel.setBackground(colors[3]);
      rightPanel.add(mixBut);
      mixBut.setBounds(0, 0, bw, bh);
      rightPanel.add(resetBut);
      resetBut.setBounds(bw, 0, bw, bh);
      rightPanel.add(editBut);
      editBut.setBounds(bw + bw, 0, bw, bh);
      rightPanel.add(solveBut);
      solveBut.setBounds(bw + bw + bw, 0, gw - 3 * bw, bh);
      rightPanel.add(viewBut);
      viewBut.setBounds(0, vh - bh, bw + bw, bh);
      rightPanel.add(viewResetBut);
      viewResetBut.setBounds(bw + bw, vh - bh, gw - bw - bw, bh);

      // add all right panel listeners
      mixBut.addActionListener(this);
      resetBut.addActionListener(this);
      editBut.addActionListener(this);
      solveBut.addActionListener(this);
      viewBut.addActionListener(this);
      viewResetBut.addActionListener(this);

      // Build set of tabpanels
      tabSet = new TabSet(this, new Color(128, 128, 128), colors[1]);
      tabSet.setBackground(colors[3]);
      rightPanel.add(tabSet);
      tabSet.setBounds(0, bh + 1, gw, bh - 1);
      tabSet.addTab("Groups", tabPanel[0]);
      tabSet.addTab("Symmetries", tabPanel[1]);
      for (i = 0; i < 2; i++) {
         tabPanel[i].setLayout(null);
         tabPanel[i].setBounds(0, bh + bh, gw, vh - bh - bh - bh - 2);
         tabPanel[i].setBackground(colors[1]);
         rightPanel.add(tabPanel[i]);
      }

      // build group tab panel
      d = tabPanel[0].getSize().height / (solvers.length * 2 + 3);
      for (i = 0; i < solvers.length; i++) {
         groupCheckBox[i] = new Checkbox();
         groupCheckBox[i].setState(true);
         groupCheckBox[i].setEnabled(false);
         groupRadioBox[i].addItemListener(this);
         tabPanel[0].add(groupCheckBox[i]);
         tabPanel[0].add(groupRadioBox[i]);

         groupRadioBox[i].setBounds(0, 3 * d + 2 * d * i, gw - 25, 2 * d);
         groupCheckBox[i].setBounds(gw - 25, 3 * d + 2 * d * i, 25, 2 * d);

         groupRadioBox[i].setBackground(colors[0]);
         groupCheckBox[i].setBackground(colors[0]);
      }
      tabPanel[0].add(superBox);
      superBox.setBounds(25, d, gw - 25, d + d);
      superBox.setBackground(colors[1]);
      // add all further group tab listeners
      superBox.addItemListener(this);

      // build symmetries tab panel
      d = tabPanel[1].getSize().height / 9;
      for (i = 0; i < 29; i++) {
         symButton[i] = new SymButton(this, 1 << i);
         tabPanel[1].add(symButton[i]);
         symButton[i].setBackground(new Color(208, 208, 208));

         y = i;
         x = 0;
         if (y >= 23) {
            y -= 23;
            x = 4;
         } else if (y >= 19) {
            y -= 17;
            x = 2;
         } else if (y >= 13) {
            y -= 13;
            x = 3;
         } else if (y >= 7) {
            y -= 7;
            x = 1;
         } else if (y > 0) {
            y -= 1;
            x = 0;
         } else {
            x = 2;
         }
         y = y * d + d;
         x *= gw / 5;
         symButton[i].setBounds(x, y + d + d, gw / 5 - 1, d - 1);
      }

      Label l = new Label("Current:");
      l.setBounds(0, 0, bw, d + d);
      tabPanel[1].add(l);
      symCurrentImage.setBounds(bw, 0, bw, d + d);
      symCurrentImage.setEnabled(false);
      tabPanel[1].add(symCurrentImage);

      l = new Label("Selected:");
      l.setBounds(bw + bw, 0, bw, d + d);
      tabPanel[1].add(l);
      symAllImage.setBounds(bw * 3, 0, bw, d + d);
      symAllImage.setEnabled(false);
      tabPanel[1].add(symAllImage);

      symTwoColBox.setBounds(5, d + d, bw * 3 - 5, bh);
      tabPanel[1].add(symTwoColBox);
      symResetBut.setBounds(bw * 3, d + d, bw, bh);
      tabPanel[1].add(symResetBut);
      symResetBut.setBackground(new Color(208, 208, 208));
      // add all symmetries tab listeners
      symTwoColBox.addItemListener(this);
      symResetBut.addActionListener(this);

      // build solution box panel
      boxPanel.setLayout(null);
      boxPanel.add(gensolBut);
      gensolBut.setBounds(0, 0, bw2, bh);
      boxPanel.add(textBox);
      textBox.setBounds(bw2, 0, vw + gw - bw2, bh);

      boxPanel.add(beginBut);
      beginBut.setBounds(bw2 + 0, bh, bw, bh2);
      boxPanel.add(backBut);
      backBut.setBounds(bw2 + bw, bh, bw, bh2);
      boxPanel.add(revBut);
      revBut.setBounds(bw2 + bw * 2, bh, bw, bh2);
      boxPanel.add(playBut);
      playBut.setBounds(bw2 + bw * 3, bh, bw, bh2);
      boxPanel.add(stepBut);
      stepBut.setBounds(bw2 + bw * 4, bh, bw, bh2);
      boxPanel.add(endBut);
      endBut.setBounds(bw2 + bw * 5, bh, bw, bh2);

      Button aboutBut = new Button("About");
      boxPanel.add(aboutBut);
      aboutBut.setBounds(vw + gw - bw, th - bh, bw, bh);
      final CubieWindow cw = this;
      aboutBut.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(cw, "Copyright 2003, 2004, 2018\nJaap Scherphuis, puzzles@jaapsch.net\nhttps://www.jaapsch.net/");
         }
      });

      boxPanel.setBackground(colors[3]);

      // add all solution box panel listeners
      gensolBut.addActionListener(this);
      textBox.addActionListener(this);
      textBox.addFocusListener(this);
      beginBut.addActionListener(this);
      backBut.addActionListener(this);
      revBut.addActionListener(this);
      playBut.addActionListener(this);
      stepBut.addActionListener(this);
      endBut.addActionListener(this);

      // initialise all solvers
      for (i = 0; i < solvers.length; i++) {
         new Thread(solvers[i]).start();
      }
      cubeViewers[viewer].repaint();
      updateStatus(false);
   }

   public void stop() {
      // tell thread to stop
      if (isPlaying) {
         isPlaying = false;
         while (moveInProgress) {
            try {
               Thread.sleep(50);
            } catch (Exception ignored) {
            }
         }
         Cubie.settings.lockViewer = false;
      }
   }

   public void run() {
      int f, q;
      isPlaying = true;
      Cubie.settings.lockViewer = true;
      do {
         if (playFw) {
            f = generator.getMoves()[seqPos];
            q = generator.getAmount()[seqPos];
            if (cubeViewers[viewer].showMove(f, q)) {
               moveInProgress = (viewer == 0);
               seqPos++;
            }
            if (seqPos >= generator.getLength())
               isPlaying = false;
         } else {
            seqPos--;
            f = generator.getMoves()[seqPos];
            q = generator.getAmount()[seqPos];
            if (!cubeViewers[viewer].showMove(f, 4 - q)) {
               seqPos++;
            } else {
               moveInProgress = (viewer == 0);
            }
            if (seqPos <= 0)
               isPlaying = false;
         }

         textBox.setText(generator.toString(solution, seqPos));
         if (isPlaying) {
            try {
               Thread.sleep(500);
            } catch (Exception ignored) {
            }
            do {
               try {
                  Thread.sleep(50);
               } catch (Exception ignored) {
               }
            } while (moveInProgress);
         }
      } while (isPlaying);
      Cubie.settings.lockViewer = false;
   }

   private void updateStatus(boolean changed) {
      // Update status of current tab and solver buttons
      boolean t;
      if (tabSet.getTab() == 0) {
         boolean currentSolvable = false;
         for (int i = 0; i < solvers.length; i++) {
            if (changed) {
               t = solvers[i].setPosition(Cubie.settings.cubePos, true);
               groupCheckBox[i].setState(t);
            } else {
               t = groupCheckBox[i].getState();
            }
            if (i == Cubie.settings.group)
               currentSolvable = t;
            int c = 1;
            if (!solvers[i].isPrepared())
               c = 0;
            else if (solvers[i].isRunning())
               c = 2;
            groupCheckBox[i].setBackground(colors[c]);
            this.groupRadioBox[i].setBackground(colors[c]);
         }
         t = solvers[Cubie.settings.group].isPrepared() && currentSolvable;
      } else {
         if (changed) {
            t = solvers[0].setPosition(Cubie.settings.cubePos, true);
         } else {
            t = groupCheckBox[0].getState();
         }
         t = solvers[0].isPrepared() && t;
         symCurrentImage.setType(Cubie.settings.cubePos.getSym());
      }
      solveBut.setLabel(Cubie.settings.solving ? "Stop" : "Solve");
      solveBut.setEnabled(t || Cubie.settings.solving);
   }

   public void destroy() {
      stop();
      if (Cubie.settings.solving) {
         // tell solver to stop
         solvers[Cubie.settings.group].stopSolving();
         // wait till it has indeed stopped
         while (Cubie.settings.solving) {
            try {
               Thread.sleep(100);
            } catch (Exception ignored) {
            }
         }
      }
      for (int i = 0; i < cubeViewers.length; i++) {
         remove(cubeViewers[i]);
      }
      remove(rightPanel);
      remove(boxPanel);
   }

   // --- button action/listening routines ---
   public void solve() {
      int g = (tabSet.getTab() == 0) ? Cubie.settings.group : 0;
      if (Cubie.settings.solving) {
         for (int i = 0; i < solvers.length; i++)
            solvers[i].stopSolving();
      } else if (solvers[g].setPosition(Cubie.settings.cubePos, false)) {
         startSolving();
         new Thread(solvers[g]).start();
      }
   }

   public void actionPerformed(ActionEvent e) {
      int i;
      Object src = e.getSource();
      if (src == mixBut) {
         if (!Cubie.settings.solving) {
            stop();// stop any animation
            if (tabSet.getTab() == 0) {
               solvers[Cubie.settings.group].mix(Cubie.settings.cubePos);
            } else {
               Cubie.settings.cubePos.mix(symType, Cubie.settings.superGroup, symTwoCol);
            }
            setSequencePosition(-1);
            updateStatus(true);
            cubeViewers[viewer].repaint();
         }
      } else if (src == resetBut) {
         if (!Cubie.settings.solving) {
            stop();// stop any animation
            Cubie.settings.cubePos.reset();
            setSequencePosition(-1);
            updateStatus(true);
            cubeViewers[viewer].repaint();
         }
      } else if (src == solveBut) {
         stop();// stop any animation
         solve();
      } else if (src == gensolBut) {
         solution = !solution;
         gensolBut.setLabel(solution ? "Solution" : "Generator");
         if (generator == null) {
            textBox.setText("");
         } else {
            textBox.setText(generator.toString(solution, seqPos));
         }
      } else if (src == viewBut) {
         viewer++;
         if (viewer >= cubeViewers.length)
            viewer = 0;
         for (i = 0; i < cubeViewers.length; i++) {
            cubeViewers[i].setVisible(i == viewer);
         }
      } else if (src == viewResetBut) {
         Cubie.settings.cubePos.resetView();
         for (i = 0; i < cubeViewers.length; i++) {
            cubeViewers[i].reset();
         }
         updateStatus(false);
      } else if (src == editBut) {
         Cubie.settings.edit = !Cubie.settings.edit;
         editBut.setLabel(Cubie.settings.edit ? "Play" : "Edit");
         seqPos = -1;
      } else if (src == textBox) {
         // change focus, forcing focus event to be processed
         groupRadioBox[Cubie.settings.group].requestFocus();
      } else if (src == beginBut) {
         stop();
         setSequencePosition(
               (generator != null && solution) ? generator.getLength() : 0);
         cubeViewers[viewer].repaint();
      } else if (src == backBut) {
         if (solution)
            stepForward();
         else
            stepBackward();
      } else if (src == revBut) {
         if (solution)
            playForward();
         else
            playBackward();
      } else if (src == playBut) {
         if (solution)
            playBackward();
         else
            playForward();
      } else if (src == stepBut) {
         if (solution)
            stepBackward();
         else
            stepForward();
      } else if (src == endBut) {
         stop();
         setSequencePosition(
               (generator == null || solution) ? 0 : generator.getLength());
         cubeViewers[viewer].repaint();
      } else if (src == symResetBut) {
         // reset all buttons
         for (i = 0; i < 29; i++)
            symButton[i].setPressed(false);
         symType = 0;
         symAllImage.setType(symType);
      } else if (src == tabSet) {
         updateStatus(true);
      } else {
         // check for viewer actions
         for (i = 0; i < cubeViewers.length; i++) {
            if (src == cubeViewers[i]) {
               updateStatus(true);
               if (e.getActionCommand() == "user") {
                  seqPos = -1;
                  if (generator != null)
                     textBox.setText(generator.toString(solution, seqPos));
               } else {
                  moveInProgress = false;
               }
               return;
            }
         }
         // check for solver actions
         for (i = 0; i < solvers.length; i++) {
            if (src == solvers[i]) {
               if (e.getActionCommand() == "a") { // init done
               } else if (e.getActionCommand() == "b") { // solution found
                  stoppedSolving();
                  generator = Cubie.settings.generator;
                  Cubie.settings.generator = null;
                  seqPos = generator.getLength();
                  textBox.setText(generator.toString(solution, seqPos));
                  Cubie.settings.cubePos.doSequence(generator);
               } else if (e.getActionCommand() == "c") { // aborted solve
                  stoppedSolving();
               } else if (e.getActionCommand() == "d") { // ended solve
                  stoppedSolving();
               } else if (e.getActionCommand() == "e") { // started solve
               }
               updateStatus(false);
               return;
            }
         }

         // check for symbutton actions
         for (i = 0; i < 29; i++) {
            if (src == symButton[i]) {
               if (e.getActionCommand() != "") { // perform ref/rot
                  if (!Cubie.settings.lockViewer) {
                     if (seqPos >= 0 && generator != null) {
                        generator.doSym(i);
                        textBox.setText(generator.toString(solution, seqPos));
                     }
                     Cubie.settings.cubePos.doSym(i, e.getActionCommand() == "c");
                     updateStatus(true);
                     cubeViewers[viewer].repaint();
                  }
               } else {
                  if (!symButton[i].isPressed()) {
                     symType |= 1 << i;
                     symButton[i].setPressed(true);
                  } else {
                     symType &= ~(1 << i);
                     symButton[i].setPressed(false);
                  }
                  symAllImage.setType(symType);
               }
               return;
            }
         }
      }
   }

   // enable all buttons
   void stoppedSolving() {
      Cubie.settings.lockViewer = false;
      textBox.setEnabled(true);
      mixBut.setEnabled(true);
      resetBut.setEnabled(true);
      superBox.setEnabled(true);

      playBut.setEnabled(true);
      revBut.setEnabled(true);
      stepBut.setEnabled(true);
      backBut.setEnabled(true);
      endBut.setEnabled(true);
      beginBut.setEnabled(true);
   }

   // disable all buttons
   void startSolving() {
      Cubie.settings.lockViewer = true;
      textBox.setEnabled(false);
      mixBut.setEnabled(false);
      resetBut.setEnabled(false);
      superBox.setEnabled(false);

      playBut.setEnabled(false);
      revBut.setEnabled(false);
      stepBut.setEnabled(false);
      backBut.setEnabled(false);
      endBut.setEnabled(false);
      beginBut.setEnabled(false);
   }

   private void stepForward() {
      if (isPlaying) {
         stop();
      } else if (generator != null && seqPos < generator.getLength()) {
         if (seqPos < 0)
            setSequencePosition(0);
         int f = generator.getMoves()[seqPos];
         int q = generator.getAmount()[seqPos];
         if (cubeViewers[viewer].showMove(f, q))
            seqPos++;
         textBox.setText(generator.toString(solution, seqPos));
      }
   }

   private void stepBackward() {
      if (isPlaying) {
         stop();
      } else if (generator != null) {
         if (seqPos < 0)
            setSequencePosition(generator.getLength());
         if (seqPos > 0) {
            seqPos--;
            int f = generator.getMoves()[seqPos];
            int q = generator.getAmount()[seqPos];
            if (!cubeViewers[viewer].showMove(f, 4 - q))
               seqPos++;
            textBox.setText(generator.toString(solution, seqPos));
         }
      }
   }

   private void playForward() {
      if (isPlaying) {
         stop();
      } else if (generator != null && seqPos < generator.getLength()) {
         if (seqPos < 0)
            setSequencePosition(0);
         playFw = true;
         new Thread(this).start();
      }
   }

   private void playBackward() {
      if (isPlaying) {
         stop();
      } else if (generator != null) {
         if (seqPos < 0)
            setSequencePosition(generator.getLength());
         if (seqPos > 0) {
            playFw = false;
            new Thread(this).start();
         }
      }
   }

   private void setSequencePosition(int p0) {
      int p = p0;
      if (generator == null) {
         textBox.setText("");
         seqPos = -1;
      } else {
         if (p > generator.getLength())
            p = generator.getLength();
         seqPos = p;
         if (p >= 0)
            Cubie.settings.cubePos.doSequence(generator, p);
         textBox.setText(generator.toString(solution, seqPos));
         updateStatus(true);
      }
   }

   public void itemStateChanged(ItemEvent e) {
      int i;
      Object src = e.getSource();
      if (src == superBox) {
         Cubie.settings.superGroup = !Cubie.settings.superGroup;
         // now set box to reflect actual choice of group
         superBox.setState(Cubie.settings.superGroup);
         // update group solvability flags
         updateStatus(true);
         // update view
         cubeViewers[viewer].repaint();
         return;
      } else if (src == symTwoColBox) {
         symTwoCol = !symTwoCol;
         // set box to reflect actual choice
         symTwoColBox.setState(symTwoCol);
         return;
      }
      for (i = 0; i < solvers.length; i++) {
         if (src == groupRadioBox[i]) {
            Cubie.settings.group = i;
            // now set box to reflect actual choice of group
            cubeGroup.setSelectedCheckbox(groupRadioBox[Cubie.settings.group]);
            // update (group solvability flags and) solve button
            updateStatus(false);
            return;
         }
      }
   }

   public void focusLost(FocusEvent e) {
      Object src = e.getSource();
      if (src == textBox) {
         textChanged();
      }
   }

   public void focusGained(FocusEvent e) {
   }

   private void textChanged() {
      if (!Cubie.settings.solving) {
         if (generator == null)
            generator = new MoveSequence();
         generator.parse(textBox.getText(), solution);
         seqPos = generator.getLength();
         textBox.setText(generator.toString(solution, seqPos));
         Cubie.settings.cubePos.doSequence(generator);
         updateStatus(true);
         cubeViewers[viewer].repaint();
      }
   }
}
