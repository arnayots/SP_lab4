package com.company;


import java.util.Scanner;

class JThread extends Thread {

    JThread(String name){
        super(name);
    }

    public void run(){

        System.out.printf("%s started... \n", Thread.currentThread().getName());
        try{
            Thread.sleep(500);
        }
        catch(InterruptedException e){
            System.out.println("Thread has been interrupted");
        }
        System.out.printf("%s fiished... \n", Thread.currentThread().getName());
    }
}


class results {
    private boolean res_f;
    private boolean res_g;

    results(){
    }

    public synchronized boolean get_f(){
        return res_f;
    }

    public synchronized boolean get_g(){
        return res_g;
    }

    public synchronized void set_f(boolean val){
        res_f = val;
    }

    public synchronized void set_g(boolean val){
        res_g = val;
    }
}

abstract class my_Thread extends Thread {

    int x;
    results res = null;

    public my_Thread(){
        super();
    }

    public my_Thread(String s, results r){
        super(s);
        res = r;
    }

    public void set_arg(int arg){
        x = arg;
    }

    protected abstract void calculate();

    public void run(){
        System.out.printf("%s started... \n", Thread.currentThread().getName());
        while(!isInterrupted()){
            try{
                calculate();
                Thread.sleep(1);
                interrupt();
            }
            catch(InterruptedException e){
                System.out.println(getName() + " has been interrupted");
                interrupt();
            }
        }
        System.out.printf("%s finished... \n", Thread.currentThread().getName());
    }



    /*
    private void calculate(){
        //res.set_f(x > 10);
    }*/
}

class Thread_f extends my_Thread {

    public Thread_f(String s, results res) {
        super(s, res);
    }

    protected void calculate(){
        res.set_f(x > 10);
    }
}

class Thread_g extends Thread {

    int x;
    results res = null;

    Thread_g(String s, results r){
        super(s);
        res = r;
    }

    public void set_arg(int arg){
        x = arg;
    }

    public void run(){
        System.out.printf("%s started... \n", Thread.currentThread().getName());

        res.set_g(x < 15);
        System.out.printf("%s finished... \n", Thread.currentThread().getName());
    }
}

public class Main {
    private static boolean ask_cont(Scanner in, String s){
        System.out.println("Function " + s + " executes too long. Continue(y/n)?");
        String tmp = in.next();
        if(!tmp.isEmpty()){
            char c = tmp.charAt(0);
            return (c == 'y' || c == 'Y');
        }
        return false;
    }

    public static void main(String[] args) {
	// write your code here

        System.out.println("Main thread started...");/*
        JThread t1= new JThread("JThread 1");
        JThread t2= new JThread("JThread 2");
        t1.start();
        t2.start();*/

        Scanner in = new Scanner(System.in);
        System.out.println("Please type (int) x: ");
        int x = 0;
        try{
            x = in.nextInt();
        }
        catch (Exception e){
            System.out.println(e.getCause());
        }

        results res = new results();
        Thread_f f1 = new Thread_f("f1", res);
        Thread_g g1 = new Thread_g("g1", res);

        f1.set_arg(x);
        g1.set_arg(x);

        f1.start();
        g1.start();

        boolean do_next = true;
        int timer = 0;
        int wakeup = 10000;
        int step = 150;
        try{
            while (do_next){
                if(!f1.isAlive() && !g1.isAlive())
                    do_next = false;
                if(timer > wakeup){
                    if(f1.isAlive()){
                        if(!ask_cont(in,"f(x)"));
                            f1.interrupt();
                    }
                    if(g1.isAlive()){
                        if(!ask_cont(in,"g(x)"));
                        g1.interrupt();
                    }
                    timer = 0;
                }
                Thread.sleep(step);
                timer += step;
            }

            System.out.println("f(x) = " + res.get_f());
            System.out.println("g(x) = " + res.get_g());

            boolean result = res.get_f() && res.get_g();
            System.out.println("Result = " + result);

        }
        catch(InterruptedException e){
/*
            System.out.printf("%s has been interrupted", t1.getName());
            System.out.printf("%s has been interrupted", t2.getName());

 */
            System.out.println(e.getCause());
        }
        System.out.println("Main thread finished...");
    }
}
