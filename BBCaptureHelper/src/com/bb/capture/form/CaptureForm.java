package com.bb.capture.form;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class CaptureForm extends JFrame {
	
	private static int borderWidth = 5;
	
	private static int px = 640;
	private static int py = 480;
	
	private static int mouseMode = -1;
	private final static int MOUSE_MODE_RESIZE = 1;
	private final static int MOUSE_MODE_MOVE = 2;
	
	private static JFrame basicForm = null;
	private int mx = -1;
	private int my = -1;
	
	private Color borderColor = new Color(0, 0, 255);
	
	private static int oldFormX = -1;
	private static int oldFormY = -1;
	private static int oldFormWidth = -1;
	private static int oldFormHeight = -1;
	
	private static Robot robot = null;
	private static String imgExtension = "png";
	
	public CaptureForm() {
		basicForm = this;
		
		try {
			robot = new Robot();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.setUndecorated(true);
		this.setBackground(new Color(0, 0, 0, 0));
		this.setVisible(true);
		
		// 반투명 배경색
		// this.setBackground(new Color(0, 0, 0, 100));
		
		this.setAlwaysOnTop(true);
		this.setSize(640, 480);
		this.setBounds(0, 0, 640, 480);
		
		this.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				
				int axisX = (int) (basicForm.getSize().getWidth());
				int axisY = (int) (basicForm.getSize().getHeight());
				
				int minX = axisX - 20;
				int maxX = axisX + 20;
				
				int minY = axisY - 20;
				int maxY = axisY + 20;
				
				int ex = e.getX();
				int ey = e.getY();

				if (minX <= ex && ex <= maxX && minY <= ey && ey <= maxY) {
					Cursor moveCursor = new Cursor(Cursor.NW_RESIZE_CURSOR);
					setCursor(moveCursor);
					
					mouseMode = MOUSE_MODE_RESIZE;
					
				} else {
					Cursor moveCursor = new Cursor(Cursor.MOVE_CURSOR);
					setCursor(moveCursor);
					
					mouseMode = MOUSE_MODE_MOVE;
				}
				
			}
	
			
			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (mouseMode == MOUSE_MODE_RESIZE) {
					if (mx > -1 && my > -1) {
						int newMx = e.getX();
						int newMy = e.getY();
						
						int resultX = newMx;
						int resultY = newMy;
						
						px = resultX;
						py = resultY;
						
						basicForm.setSize(resultX, resultY);
						basicForm.repaint();
					}
					
				} else if (mouseMode == MOUSE_MODE_MOVE) {
					if (mx > -1 && my > -1) {
						int newMx = e.getX();
						int newMy = e.getY();
						
						int resultX = ((int) (basicForm.getLocation().getX()) + (newMx - mx));
						int resultY = ((int) (basicForm.getLocation().getY()) + (newMy - my));
						
						basicForm.setLocation(resultX, resultY);
					}
				}
			}
		});
		
		
		this.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				mx = -1;
				my = -1;
			}
			
			
			@Override
			public void mousePressed(MouseEvent e) {
				mx = e.getX();
				my = e.getY();
			}
			
			
			@Override
			public void mouseExited(MouseEvent e) {
				
			}
			
			
			@Override
			public void mouseEntered(MouseEvent e) {
				
			}
			
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
			}
		});
		
		
		this.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// 알파벳 p를 누르면 실행
				if (e != null && e.getKeyCode() == 80) {
					doCapture(false);
				}
			}
		});
	}
	
	
	public void paint(Graphics g) {
		super.paint(g);
		
		g.setColor(borderColor);
		
		for (int i=0; i<borderWidth; i++) {
			g.drawRect(i, i, px - (i + i), py - (i + i));
		}
	}
	
	
	public static void doCapture(boolean autoCapture) {
		try {
			int formX = (int) basicForm.getBounds().getX();
			int formY = (int) basicForm.getBounds().getY();
			int formWidth = (int) basicForm.getContentPane().getWidth();
			int formHeight = (int) basicForm.getContentPane().getHeight();
			 
			System.out.println("formX : " + formX);
			System.out.println("formY : " + formY);
			System.out.println("formWidth : " + formWidth);
			System.out.println("formHeight : " + formHeight);
			
			System.out.println("px : " + px);
			System.out.println("py : " + py);
			
			int oldFileIndex = 1;
			File oldFile = null;
			for (int i=999; i>0; i--) {
				File file = new File("c:/a/res" + i + "." + imgExtension);
				if (file.exists()) {
					oldFile = file;
					oldFileIndex = i;
					break;
				}
			}
			
			File newFile = null;
			for (int i=oldFileIndex + 1; i<1000; i++) {
				File file = new File("c:/a/res" + i + "." + imgExtension);
				if (!file.exists()) {
					newFile = file;
					break;
				}
			}
			
			// 기존 이미지가 있는 경우 읽어온다.
			BufferedImage oldScreenImage = null;
			if (oldFile != null && oldFile.exists()) {
				oldScreenImage = ImageIO.read(oldFile);
			}
			
			// 새 이미지를 스크린샷한다.
			BufferedImage newScreenImage = robot.createScreenCapture(new Rectangle(formX + borderWidth, formY + borderWidth, formWidth - (borderWidth * 2), formHeight - (borderWidth * 2)));
			
			// 연속된 이미지인지 확인 필요한 경우 bCheckSerialImage 변수를 true로 지정.
			boolean bCheckSerialImage = false;
			if (oldScreenImage != null && newScreenImage != null) {
				// 그림의 가로크기가 변하지 않은경우 연속된 이미지라고 본다.
				if (oldScreenImage.getWidth() == newScreenImage.getWidth()) {
					// 높이가 480은 되어야지 그림을 이어붙일 수 있다.
					if (oldScreenImage.getHeight() >= 470) {
						// 사각형의 크기와 위치가 변하지 않은 경우 연속된 이미지라고 본다.
						if (oldFormWidth == formWidth && oldFormHeight == formHeight) {
							if (oldFormX == formX && oldFormY == formY) {
								bCheckSerialImage = true;
							}
						}
					}
				}
			}
			
			// 기존 이미지의 하단 세로길이 200 크기만큼의 RGB값으로 새 이미지에서 동일한 영역을 찾는다.
			int mergeHeight = 200;
			
			// 연속된 이미지인지 확인한다.
			if (bCheckSerialImage) {
				int oldImgWidth = oldScreenImage.getWidth();
				int oldImgHeight = oldScreenImage.getHeight();
				
				ArrayList<Integer> oldImageRGBList = new ArrayList<Integer>();
				for (int w=0; w<oldImgWidth; w++) {
					for (int h=(oldImgHeight-mergeHeight); h<oldImgHeight; h++) {
						oldImageRGBList.add(oldScreenImage.getRGB(w, h));
					}
				}
				
				int newImgWidth = newScreenImage.getWidth();
				int newImgHeight = newScreenImage.getHeight();
				
				// 뉴 이미지에서 위 영역을 찾는다.
				boolean bAllSame = true;
				int axisH = 0;
				
				try {
					while (true) {
						bAllSame = true;
						int oldRGBIndex = 0;
						
						if ((axisH + mergeHeight) >= newImgHeight) {
							bAllSame = false;
							break;
						}
						
						outLoop : for (int w=0; w<newImgWidth; w++) {
							for (int h=axisH; h<(axisH+mergeHeight); h++) {
								if (oldImageRGBList.get(oldRGBIndex++) != newScreenImage.getRGB(w, h)) {
									bAllSame = false;
									break outLoop;
								}
							}
						}
						
						if (bAllSame) {
							break;
						}
						
						axisH++;
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				if (bAllSame && oldFile != null) {
					// 그림 2개를 합친다.
					int addingHeight = newImgHeight - axisH;
					
					BufferedImage mergedImg = new BufferedImage(oldImgWidth, oldImgHeight + addingHeight - mergeHeight, BufferedImage.TYPE_INT_RGB);
					Graphics2D graphics = mergedImg.createGraphics();
					graphics.fillRect(0, 0, oldImgWidth, oldImgHeight + addingHeight - mergeHeight);
					
					for (int w=0; w<oldImgWidth; w++) {
						for (int h=0; h<oldImgHeight; h++) {
							mergedImg.setRGB(w, h, oldScreenImage.getRGB(w, h));
						}
					}
					
					for (int w=0; w<newImgWidth; w++) {
						for (int h=0; h<addingHeight; h++) {
							mergedImg.setRGB(w, h + (oldImgHeight - mergeHeight), newScreenImage.getRGB(w, h + axisH));
						}
					}
					
					long l1 = oldFile.lastModified();
					long l2 = 0;
					ImageIO.write(mergedImg, imgExtension, oldFile);
					
					// 그림이 겹치는 경우 합치기 작업 이후, 자동으로 연속캡쳐
					if (autoCapture) {
						while (l1 > l2) {
							Thread.sleep(100);
							if (oldFile.exists() && oldFile.canWrite()) {
								l2 = oldFile.lastModified();
							}
						}
						
						robot.mouseWheel(1); // 자동스크롤
						//doCapture(true);
					}
					
				} else {
					if (autoCapture) {
						
					} else {
						// 겹치는 영역을 찾지 못했으면 새 그림을 쓴다.
						if (newFile != null) {
							ImageIO.write(newScreenImage, imgExtension, newFile);
						} else {
							System.err.println("newFile is null.");
						}
					}
				}
				
			} else {
				// 최초 캡쳐
				// 겹치는 영역을 찾지 못했으면 새 그림을 쓴다.
				if (newFile != null) {
					long l1 = newFile.lastModified();
					long l2 = 0;
					ImageIO.write(newScreenImage, imgExtension, newFile);
					
					// 최초 캡쳐 이후, 자동으로 연속캡쳐
					if (!autoCapture) {
						while (l1 > l2) {
							Thread.sleep(100);
							if (newFile.exists() && newFile.canWrite()) {
								l2 = newFile.lastModified();
							}
						}
						
						robot.mouseWheel(1); // 자동스크롤
						//doCapture(true);
					}
					
				} else {
					System.err.println("newFile is null.");
				}
			}
			
			oldFormX = formX;
			oldFormY = formY;
			oldFormWidth = formWidth;
			oldFormHeight = formHeight;
									
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
