package com.company;
import java.util.InputMismatchException;
import java.util.Scanner;

import static java.lang.Math.cos;

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

    protected abstract void calculate() throws InterruptedException;

    public void run(){
        System.out.println(getName() + " started...");
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
        System.out.println(getName() + " finished...");
    }
}

class Thread_f extends my_Thread {
    public Thread_f(String s, results res) {
        super(s, res);
    }

    protected void calculate() throws InterruptedException {
        res.set_f(cos(x) > 0.5);
        sleep(15000);
    }
}

class Thread_g extends my_Thread {
    public Thread_g(String s, results res) {
        super(s, res);
    }

    protected void calculate() throws InterruptedException {
        res.set_g(x < 15);
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
        System.out.println("Main thread started...");

        Scanner in = new Scanner(System.in);
        System.out.println("Please type (int) x: ");
        int x = 0;

        try{
            x = in.nextInt();
        }
        catch (InputMismatchException e){
            System.out.println("Error input");
            return;
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
        int wakeup = 3000;
        int step = 150;
        boolean wrong_res = false;
        try{
            while (do_next){
                if(!f1.isAlive() && !g1.isAlive())
                    do_next = false;
                if(timer > wakeup){
                    if(f1.isAlive()){
                        if(!ask_cont(in,"f(x)")) {
                            f1.interrupt();
                            wrong_res = true;
                        }
                    }
                    if(g1.isAlive()){
                        if(!ask_cont(in,"g(x)")) {
                            g1.interrupt();
                            wrong_res = true;
                        }
                    }
                    timer = 0;
                }
                Thread.sleep(step);
                timer += step;
            }

            if(!wrong_res) {

                System.out.println("f(x) = " + res.get_f());
                System.out.println("g(x) = " + res.get_g());

                boolean result = res.get_f() && res.get_g();
                System.out.println("Result = " + result);
            } else {
                System.out.println("One of processes has been interrupted");
            }
        }
        catch(InterruptedException e){
            System.out.println(e.getCause());
        }
        System.out.println("Main thread finished...");
    }
}
