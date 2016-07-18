package world.hello;

import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.java.html.BrwsrCtx;
import net.java.html.json.ComputedProperty;
import net.java.html.json.Function;
import net.java.html.json.Model;
import net.java.html.json.Models;
import net.java.html.json.OnPropertyChange;
import net.java.html.json.Property;
import world.hello.js.Dialogs;
import world.hello.js.Router;

/**
 * Model annotation generates class Data with one message property, boolean
 * property and read only words property
 */
@Model(className = "Data", targetId = "control", properties = {
    @Property(name = "message", type = String.class),
    @Property(name = "started", type = boolean.class)
})
final class DataModel {

    @Model(className = "ThreadData", targetId = "", properties = {
        @Property(name = "currentTime", type = String.class)
    })
    public class ThreadModel {

    }

    @OnPropertyChange("message")
    public static void messageChanged(Data data) {
        //This property Change is trigger from JS side in FX thread.
        //But it is not executed as BrwsCtx.execute()
        //Hence it hasn't CURRENT BrwsrCtx here
        // The viewModel we created cannot be updated from another thread.

        createViewModelInSameThread("thread4");


        createViewModelInAntherThreadAmdBind("thread5");
        
        

// Trace Back
//	at world.hello.DataModel.messageChanged(DataModel.java:37)
//	at world.hello.Data.setMessage(Data.java:18)
//	at world.hello.Data$Html4JavaType.setValue(Data.java:53)
//	at world.hello.Data$Html4JavaType.setValue(Data.java:44)
//	at org.netbeans.html.json.spi.PropertyBinding$AImpl.setValue(PropertyBinding.java:161)
//	at org.netbeans.html.ko4j.Knockout.setValue(Knockout.java:109)
//	at org.netbeans.html.ko4j.$JsCallbacks$.raw$org_netbeans_html_ko4j_Knockout$setValue$ILjava_lang_Object_2($JsCallbacks$.java:156)
//	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
//       .....
//      at com.sun.webkit.Utilities.lambda$fwkInvokeWithContext$55(Utilities.java:94)
//	at java.security.AccessController.doPrivileged(Native Method)
//	at com.sun.webkit.Utilities.fwkInvokeWithContext(Utilities.java:94)
//	at com.sun.webkit.Timer.twkFireTimerEvent(Native Method)
//	at com.sun.webkit.Timer.fireTimerEvent(Timer.java:83)
//	at com.sun.webkit.Timer.notifyTick(Timer.java:64)
//	at javafx.scene.web.WebEngine$PulseTimer.lambda$static$45(WebEngine.java:1201)
//	at com.sun.javafx.tk.Toolkit.lambda$runPulse$30(Toolkit.java:355)
//	at java.security.AccessController.doPrivileged(Native Method)
//	at com.sun.javafx.tk.Toolkit.runPulse(Toolkit.java:354)
//	at com.sun.javafx.tk.Toolkit.firePulse(Toolkit.java:381)
//	at com.sun.javafx.tk.quantum.QuantumToolkit.pulse(QuantumToolkit.java:510)
//	at com.sun.javafx.tk.quantum.QuantumToolkit.pulse(QuantumToolkit.java:490)
//	at com.sun.javafx.tk.quantum.QuantumToolkit.lambda$runToolkit$404(QuantumToolkit.java:319)
//	at com.sun.glass.ui.InvokeLaterDispatcher$Future.run(InvokeLaterDispatcher.java:95)
    }

    @Function
    public static void createViewModels(Data data) {
        //
        createViewModelInAnotherThread();
        createViewModelInSameThread("thread3");
    }

    private static Data ui;

    private static BrwsrCtx ctx;

    /**
     * Called when the page is ready.
     */
    static void onPageLoad() throws Exception {
        ui = new Data();
        Models.toRaw(ui);
        Router.registerBinding();
        ui.applyBindings();
        ctx = BrwsrCtx.findDefault(Data.class);
    }

    private static void createViewModelInAnotherThread() {
        Runnable task;
        task = new Runnable() {

            @Override
            public void run() {
                //Create viewmodel without ctx.execute
                final ThreadData data2 = new ThreadData();

                ctx.execute(new Runnable() {

                    @Override
                    public void run() {
                        Models.applyBindings(data2, "thread2");
                    }
                });
                while (true) {
                    data2.setCurrentTime(ZonedDateTime.now().toString());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        };

        new Thread(task).start();

    }

    private static void createViewModelInSameThread(final String divId) {
        final ThreadData data = new ThreadData();
        data.setCurrentTime(ZonedDateTime.now().toString());

//        Models.applyBindings(data, divId);
        Runnable task = new Runnable() {

            @Override
            public void run() {

                ctx.execute(new Runnable() {

                    @Override
                    public void run() {
                        Models.applyBindings(data, divId);
                    }
                });

                while (true) {
                    data.setCurrentTime(ZonedDateTime.now().toString());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        };

        new Thread(task).start();

    }
    
     private static void createViewModelInAntherThreadAmdBind(final String divId) {

//        Models.applyBindings(data, divId);
        Runnable task = new Runnable() {

            @Override
            public void run() {
                ThreadData copy = new ThreadData();
                copy.setCurrentTime(ZonedDateTime.now().toString());
                //Change the context
                final ThreadData afterBind = Models.bind(copy, ctx);

                ctx.execute(new Runnable() {

                    @Override
                    public void run() {
                        Models.applyBindings(afterBind, divId);
                    }
                });

                while (true) {
                    afterBind.setCurrentTime(ZonedDateTime.now().toString());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        };

        new Thread(task).start();

    }

}
