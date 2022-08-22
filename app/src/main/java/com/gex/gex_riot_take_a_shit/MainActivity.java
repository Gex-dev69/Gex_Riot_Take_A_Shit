package com.gex.gex_riot_take_a_shit;

import static com.gex.gex_riot_take_a_shit.MainActivity.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import io.github.muddz.styleabletoast.StyleableToast;


public class MainActivity extends AppCompatActivity {




    // Handler is Must to change UI_ElEMENTS outside of the  mainactivity class
    // Might not need it in Main Activity, Need to go to Fragments
    static Handler UI_Handler = new Handler();
    static FragmentManager fragmentManager;

    {
        fragmentManager = getSupportFragmentManager();
    }

    public static Current_status_Data viewModel;

    public static Context ContextMethod() {
        Context context = App.getContext();
        return context;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebsocketServer Gex = new WebsocketServer();
        setContentView(R.layout.activity_main);
        // Main Activity background color
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        Gex.start();
        // https://github.com/Nightonke/JellyToggleButton#listener
        //JellyToggleButton server_Switch = findViewById(R.id.server_switch);
        //server_Switch.setJelly(Jelly.LAZY_TREMBLE_TAIL_SLIM_JIM);
        viewModel = new ViewModelProvider(this).get(Current_status_Data.class);
        viewModel.getFor_char().observe(this,item ->{
            Gex.broadcast(item);
        });

        // STARTS HERE *  Bottom Navigation Bar https://github.com/Ashok-Varma/BottomNavigation
        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar)findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.valo,"GAME"))
                .addItem(new BottomNavigationItem(R.drawable.riot, "STORE"))
                .addItem(new BottomNavigationItem(R.drawable.controller,"Feature"))
                .setInActiveColor(R.color.Inactive_color_bottom_bar)
                .setBarBackgroundColor(R.color.Bottom_bar_color)
                .setActiveColor(R.color.Valo_Color)
                .setFirstSelectedPosition(0)
                .initialise();
        bottomNavigationBar.setElevation(0);
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                switch (position){
                    case 0:
                        Game_Status_Fragment();
                        System.out.println("first");
                        break;
                    case 1:
                        Store_Fragment();
                        System.out.println("SECOND");
                        break;
                    case 2:
                        System.out.println("Third");
                        break;
                }
            }
            @Override
            public void onTabUnselected(int position) { }
            @Override
            public void onTabReselected(int position) {}
        });
        // ENDS HERE * Bottom Navigation Bar
        // switch here man
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, In_game.class, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)       // name can be null
                .commit();
        // change this to the switch button
        /*
        server_Switch.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                // add logic with State variable
                if(state.equals(State.RIGHT)){
                    new StyleableToast
                            .Builder(ContextMethod())
                            .text("Hosted")
                            .length(2)
                            .textColor(Color.WHITE)
                            .backgroundColor(Color.BLUE)
                            .iconStart(R.drawable.riot)
                            .show();
                    viewModel.Game_state("Connected");
                    Gex.start();
                }else if (state.equals(State.LEFT)){
                    new StyleableToast
                            .Builder(ContextMethod())
                            .text("Disabled")
                            .length(1)
                            .textColor(Color.WHITE)
                            .backgroundColor(Color.RED)
                            .iconStart(R.drawable.riot)
                            .show();
                }
            }
        });

         */
    }


    public static void Agent_Select_fragment(){
        UI_Handler.post(new Runnable() {
            @Override
            public void run() {
                fragmentManager.beginTransaction()
                        //.replace(R.id.fragmentContainerView, Agent_Selection_Menu.class, null)
                        .replace(R.id.fragmentContainerView, improved_Agent_sel_fragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null) // name can be null
                        .commit();
            }
        });
    }
    public static void Game_Status_Fragment(){
        UI_Handler.post(new Runnable() {
            @Override
            public void run() {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, Game_Status.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null) // name can be null
                        .commit();
            }
        });
    }
    public static void Store_Fragment(){
        UI_Handler.post(new Runnable() {
            @Override
            public void run() {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, Store_Fragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null) // name can be null
                        .commit();

            }
        });
    }
    // Use this three functions incase you want to change a Ui element in the main_activity
}

class WebsocketServer extends WebSocketServer{

    private static int TCP_PORT = 4444;
    private Set<WebSocket> conns;
    public WebsocketServer() {
        super(new InetSocketAddress(TCP_PORT));
        conns = new HashSet<>();


    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conns.add(conn);
        System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
        /*
        new StyleableToast
                .Builder(ContextMethod())
                .text("Connected")
                .textColor(Color.WHITE)
                .backgroundColor(Color.BLUE)
                .iconStart(R.drawable.riot)
                .show();*/
        // you can't broadcast if you use toast here
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        conns.remove(conn);
        System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
        new StyleableToast
                .Builder(ContextMethod())
                .text("Disconnected")
                .textColor(Color.WHITE)
                .backgroundColor(Color.RED)
                .iconStart(R.drawable.riot)
                .show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onMessage(WebSocket conn, String message) {
        broadcast("work?");
        System.out.println("Game Log: " + message);
        for (WebSocket sock : conns) {
            sock.send(message);
        }

        //viewModel.Selection(message.toString());

        switch (message) {
            case "MainMenu":
                viewModel.Game_state("MainMenu");
            case "CharacterSelectPersistentLevel":
                Agent_Select_fragment();
            case "Game starting":
                System.out.println("Game Starting");
            case "match_start":
                System.out.println("Match Started");
            default:
                viewModel.Selection(message.toString());
        }

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        //ex.printStackTrace();
        if (conn != null) {
            conns.remove(conn);
            // do some thing if required
        }
        System.out.println(ex);
    }

    @Override
    public void onStart() {
        System.out.println("started");

    }

    @Override
    public  void stop(){

    }
    @Override
    public void broadcast(String text) {
        broadcast(text, conns);
    }

}