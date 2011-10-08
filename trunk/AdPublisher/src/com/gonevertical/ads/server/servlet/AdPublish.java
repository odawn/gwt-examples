package com.gonevertical.ads.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gonevertical.ads.server.jdo.PickJdo;

public class AdPublish extends HttpServlet {

  private ArrayList<String> a;
  
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException  {

    set();
    
   
    String s = "<html><body>";
    s += pick();
    s += "</body></html>";

    // output the iframe
    res.setContentType("text/html"); 
    PrintWriter out = res.getWriter();
    out.print(s);
  }
  
  private String pick() {
    
    String[] picking = new String[a.size()];
    a.toArray(picking);

    String s = picking[loadNum()];
    
    return s;
  }

  private int loadNum() {
    int n = PickJdo.queryPick("affAds", a.size()-1);
    return n;
  }

  private void set() {
    a = new ArrayList<String>();
    
    // 0 dvd
    a.add("<iframe src=\"http://rcm.amazon.com/e/cm?t=foreclo07-20&o=1&p=48&l=ur1&category=dvd&banner=0DE2PBSZ1XXT9HH45SG2&f=ifr\" width=\"728\" height=\"90\" scrolling=\"no\" border=\"0\" marginwidth=\"0\" style=\"border:none;\" frameborder=\"0\"></iframe>");

    // irobot
    a.add("<a href=\"http://gan.doubleclick.net/gan_click?lid=41000000035671207&pubid=21000000000501635\"><img src=\"http://gan.doubleclick.net/gan_impression?lid=41000000035671207&pubid=21000000000501635\" border=0 alt=\"Shop The 700 Series Roomba at irobot.com!\"></a>");
    
    // 1 gourmet food
    a.add("<iframe src=\"http://rcm.amazon.com/e/cm?t=foreclo07-20&o=1&p=48&l=ur1&category=gourmet&banner=0DSWRZ5A2FXV23WJNK02&f=ifr\" width=\"728\" height=\"90\" scrolling=\"no\" border=\"0\" marginwidth=\"0\" style=\"border:none;\" frameborder=\"0\"></iframe>");
    
    // 2 amazon wireless
    a.add("<iframe src=\"http://rcm.amazon.com/e/cm?t=foreclo07-20&o=1&p=48&l=ur1&category=amazonwireless&banner=00JTJ4M6VV0HD6JHEV82&f=ifr\" width=\"728\" height=\"90\" scrolling=\"no\" border=\"0\" marginwidth=\"0\" style=\"border:none;\" frameborder=\"0\"></iframe>");
    
    // 3 watches
    a.add("<iframe src=\"http://rcm.amazon.com/e/cm?t=foreclo07-20&o=1&p=48&l=ur1&category=watches&banner=0HDTHZ74DP3VKQNN4PR2&f=ifr\" width=\"728\" height=\"90\" scrolling=\"no\" border=\"0\" marginwidth=\"0\" style=\"border:none;\" frameborder=\"0\"></iframe>");
    
    // abt cameras
    a.add("<a href=\"http://gan.doubleclick.net/gan_click?lid=41000000030827630&pubid=21000000000501635\"><img src=\"http://gan.doubleclick.net/gan_impression?lid=41000000030827630&pubid=21000000000501635\" border=0 alt=\"Cameras\"></a>");
    
    // 4 software
    a.add("<iframe src=\"http://rcm.amazon.com/e/cm?t=foreclo07-20&o=1&p=48&l=ur1&category=software&banner=0XQS9014D4AKQSBC2F02&f=ifr\" width=\"728\" height=\"90\" scrolling=\"no\" border=\"0\" marginwidth=\"0\" style=\"border:none;\" frameborder=\"0\"></iframe>");
    
    // 5 create own ring
    a.add("<iframe src=\"http://rcm.amazon.com/e/cm?t=foreclo07-20&o=1&p=48&l=ur1&category=createyourownring&banner=0QWDG73S6F5X6CBJTG02&f=ifr\" width=\"728\" height=\"90\" scrolling=\"no\" border=\"0\" marginwidth=\"0\" style=\"border:none;\" frameborder=\"0\"></iframe>");
    
    // 6 books
    a.add("<iframe src=\"http://rcm.amazon.com/e/cm?t=foreclo07-20&o=1&p=48&l=ur1&category=books&banner=0835KA0M3CYPVY7X8MR2&f=ifr\" width=\"728\" height=\"90\" scrolling=\"no\" border=\"0\" marginwidth=\"0\" style=\"border:none;\" frameborder=\"0\"></iframe>");
    
    // 7 kindle
    a.add("<iframe src=\"http://rcm.amazon.com/e/cm?t=foreclo07-20&o=1&p=48&l=ur1&category=kindle&banner=0Y98S4SYN0MXZ8260582&f=ifr\" width=\"728\" height=\"90\" scrolling=\"no\" border=\"0\" marginwidth=\"0\" style=\"border:none;\" frameborder=\"0\"></iframe>");
    
    // 8 gifts
    a.add("<iframe src=\"http://rcm.amazon.com/e/cm?t=foreclo07-20&o=1&p=48&l=ur1&category=giftswishlists&banner=171SSX7KCG8KABZCQ5R2&f=ifr\" width=\"728\" height=\"90\" scrolling=\"no\" border=\"0\" marginwidth=\"0\" style=\"border:none;\" frameborder=\"0\"></iframe>");
    
    // 9 games
    a.add("<iframe src=\"http://rcm.amazon.com/e/cm?t=foreclo07-20&o=1&p=48&l=ur1&category=game_downloads&banner=07YBAJRZ4ZYJNPDJS3G2&f=ifr\" width=\"728\" height=\"90\" scrolling=\"no\" border=\"0\" marginwidth=\"0\" style=\"border:none;\" frameborder=\"0\"></iframe>");
    
    // 10 magazine
    a.add("<iframe src=\"http://rcm.amazon.com/e/cm?t=foreclo07-20&o=1&p=48&l=ur1&category=magazines&banner=0JEKPB5Q7WMP8GTCCT02&f=ifr\" width=\"728\" height=\"90\" scrolling=\"no\" border=\"0\" marginwidth=\"0\" style=\"border:none;\" frameborder=\"0\"></iframe>");
  }

 
  
}
