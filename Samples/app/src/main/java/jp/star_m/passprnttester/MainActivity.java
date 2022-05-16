package jp.star_m.passprnttester;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.sql.DriverManager.println;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    List<Map<String, String>> listViewItems = new ArrayList<>();
    SimpleAdapter adapter;
    private int selectedIndex;

    String m_portname         = null;
    String m_portSettings     = null;
    String m_timeout          = null;
    String m_width            = null;
    String m_drawer           = null;
    String m_pulse            = null;
    String m_buzzer           = null;
    String m_buzzerChannel    = null;
    String m_buzzerRepeat     = null;
    String m_buzzerDriveTime  = null;
    String m_buzzerDelayTime  = null;
    String m_sound            = null;
    String m_soundStorageArea = null;
    String m_soundNumber      = null;
    String m_soundVolume      = null;
    String m_callBack         = null;
    String m_blackmark        = null;
    String m_cuttype          = null;
    String m_popup            = null;

    String m_htmlReceiptData  = null;
    String m_pdfReceiptData   = null;
    String m_url              = null;

    String[] fileList;
    String[] pdfFileList;

    protected Button printButton = null;
    protected ListView listView = null;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OutputReturnValue();

        m_portname         = (m_portname        == null)  ? "none"        : m_portname;
        m_portSettings     = (m_portSettings    == null)  ? "none"        : m_portSettings;
        m_timeout          = (m_timeout         == null)  ? "none"        : m_timeout;
        m_width            = (m_width           == null)  ? "none"        : m_width;
        m_drawer           = (m_drawer          == null)  ? "none"        : m_drawer;
        m_pulse            = (m_pulse           == null)  ? "none"        : m_pulse;
        m_buzzer           = (m_buzzer          == null)  ? "none"        : m_buzzer;
        m_buzzerChannel    = (m_buzzerChannel   == null)  ? "none"        : m_buzzerChannel;
        m_buzzerRepeat     = (m_buzzerRepeat    == null)  ? "none"        : m_buzzerRepeat;
        m_buzzerDriveTime  = (m_buzzerDriveTime == null)  ? "none"        : m_buzzerDriveTime;
        m_buzzerDelayTime  = (m_buzzerDelayTime == null)  ? "none"        : m_buzzerDelayTime;
        m_sound            = (m_sound == null)            ? "none"        : m_sound;
        m_soundStorageArea = (m_soundStorageArea == null) ? "none"        : m_soundStorageArea;
        m_soundNumber      = (m_soundNumber      == null) ? "none"        : m_soundNumber;
        m_soundVolume      = (m_soundVolume      == null) ? "none"        : m_soundVolume;
        m_callBack         = (m_callBack         == null) ? "startest://" : m_callBack;
        m_blackmark        = (m_blackmark        == null) ? "none"        : m_blackmark;
        m_cuttype          = (m_cuttype          == null) ? "none"        : m_cuttype;
        m_popup            = (m_popup            == null) ? "none"        : m_popup;
        try {
            String[] strList = getResources().getAssets().list("");

            ArrayList<String> stringArrayList = new ArrayList<>();
            stringArrayList.add("none");

            ArrayList<String> stringPdfArrayList = new ArrayList<>();
            stringPdfArrayList.add("none");

            for (int i = 0; i < strList.length; i++) {
                if (strList[i].contains(".html")) {
                    stringArrayList.add(strList[i]);
                } else if (strList[i].contains(".pdf")) {
                    stringPdfArrayList.add(strList[i]);
                }
            }
            fileList    = stringArrayList.toArray(new String[0]);
            pdfFileList = stringPdfArrayList.toArray(new  String[0]);

        } catch (IOException e) {
            e.printStackTrace();
        }
        m_htmlReceiptData = "index.html";
        m_pdfReceiptData  = "none";

        m_url = (m_url == null) ? "none": m_url;

        adapter = new SimpleAdapter(
                this,
                listViewItems,
                android.R.layout.simple_list_item_2,
                new String[] {"main", "sub"},
                new int[] {android.R.id.text1, android.R.id.text2}
        );

        listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        printButton = findViewById(R.id.button);
        printButton.setOnClickListener(this);

        refreshListInfo();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:     //PortName
                        // Communicate with...
                        // "none"   : the printer is configured on setting display
                        // "bt:"    : the printer is first detected by OS
                        // "bt:xxx" : the printer is named as xxx
                        // "usb:"       : the printer is first detected by OS
                        // "usb:SN:xxx" : the printer is configured serial number as xxx
                        final String[] portNameIndexList = {"none", "bt:", "tcp:", "usb:"};

                        for (int i = 0; i < portNameIndexList.length; i++) {
                            if (portNameIndexList[i].equalsIgnoreCase(m_portname)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Port Name")
                                .setSingleChoiceItems(portNameIndexList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        m_portname = portNameIndexList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;
                    case 1:     //PortSettings
                        // Configured for communicate with
                        // "none"     : Desktop models  ex:) TSP650II
                        // "portable" : Portable models(StarPRNT mode)  ex:) SM-T300i
                        final String[] portSettingsIndexList = {"none", "blank", "portable", "portable;escpos", "escpos"};

                        for (int i = 0; i < portSettingsIndexList.length; i++) {
                            if (portSettingsIndexList[i].equalsIgnoreCase(m_portSettings)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Port Settings")
                                .setSingleChoiceItems(portSettingsIndexList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        m_portSettings = portSettingsIndexList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;
                    case 2:     //Timeout
                        // Configured for printing area
                        final String[] timeoutIndexList = {"none", "10000"};

                        for (int i = 0; i < timeoutIndexList.length; i++) {
                            if (timeoutIndexList[i].equalsIgnoreCase(m_timeout)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Timeout")
                                .setSingleChoiceItems(timeoutIndexList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        m_timeout = timeoutIndexList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;
                    case 3:     //Width
                        // Configured for printing area
                        final String[] widthIndexList = {"none", "2", "2w1", "2w2", "2w3", "2w4", "3", "3w", "3w2", "4"};

                        for (int i = 0; i < widthIndexList.length; i++) {
                            if (widthIndexList[i].equalsIgnoreCase(m_width)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Print Width")
                                .setSingleChoiceItems(widthIndexList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        m_width = widthIndexList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;
                    case 4:     //Drawer
                        final String[] drawerIndexList = {"none", "Off", "Before printing", "After printing"};

                        for (int i = 0; i < drawerIndexList.length; i++) {
                            if (drawerIndexList[i].equalsIgnoreCase(m_drawer)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Drawer")
                                .setSingleChoiceItems(drawerIndexList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        m_drawer = drawerIndexList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;
                    case 5:     //Pulse
                        final String[] pulseIndexList = {"none", "200", "500"};

                        for (int i = 0; i<pulseIndexList.length; i++) {
                            if (pulseIndexList[i].equalsIgnoreCase(m_pulse)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Drawer Pulse")
                                .setSingleChoiceItems(pulseIndexList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        m_pulse = pulseIndexList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;
                    case 6:     // Buzzer
                        final String[] buzzerList = {"none", "off", "ahead", "after"};

                        for (int i = 0; i < buzzerList.length; i++) {
                            if (buzzerList[i].equalsIgnoreCase(m_buzzer)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Buzzer")
                                .setSingleChoiceItems(buzzerList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        m_buzzer = buzzerList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;

                    case 7:     // Buzzer Channel
                        final String[] buzzerChannelList = {"none", "channel1", "channel2"};

                        for (int i = 0; i < buzzerChannelList.length; i++) {
                            if (buzzerChannelList[i].equalsIgnoreCase(m_buzzerChannel)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Buzzer Channel")
                                .setSingleChoiceItems(buzzerChannelList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        m_buzzerChannel = buzzerChannelList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;

                    case 8:     // Buzzer Repeat
                        final String[] buzzerRepeatList = {"none", "1", "2", "5"};

                        for (int i = 0; i < buzzerRepeatList.length; i++) {
                            if (buzzerRepeatList[i].equalsIgnoreCase(m_buzzerRepeat)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Buzzer Repeat")
                                .setSingleChoiceItems(buzzerRepeatList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        m_buzzerRepeat = buzzerRepeatList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;

                    case 9:     // Buzzer Drive Time
                        final String[] buzzerDriveTimeList = {"none", "200", "500", "1000"};

                        for (int i = 0; i < buzzerDriveTimeList.length; i++) {
                            if (buzzerDriveTimeList[i].equalsIgnoreCase(m_buzzerDriveTime)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Buzzer Drive Time")
                                .setSingleChoiceItems(buzzerDriveTimeList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        m_buzzerDriveTime = buzzerDriveTimeList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;

                    case 10:     // Buzzer Delay Time
                        final String[] buzzerDelayTimeList = {"none", "200", "500", "1000"};

                        for (int i = 0; i < buzzerDelayTimeList.length; i++) {
                            if (buzzerDelayTimeList[i].equalsIgnoreCase(m_buzzerDelayTime)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Buzzer Delay Time")
                                .setSingleChoiceItems(buzzerDelayTimeList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        m_buzzerDelayTime = buzzerDelayTimeList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;

                    case 11:     // Sound
                        final String[] soundList = {"none", "off", "ahead", "after"};

                        for (int i = 0; i < soundList.length; i++) {
                            if (soundList[i].equalsIgnoreCase(m_sound)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Sound")
                                .setSingleChoiceItems(soundList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        m_sound = soundList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;

                    case 12:     // Sound Storage Area
                        final String[] soundStorageAreaList = {"none", "area1", "area2"};

                        for (int i = 0; i < soundStorageAreaList.length; i++) {
                            if (soundStorageAreaList[i].equalsIgnoreCase(m_soundStorageArea)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Sound Storage Area")
                                .setSingleChoiceItems(soundStorageAreaList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        m_soundStorageArea = soundStorageAreaList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;
                    case 13:     // Sound Number
                        final String[] soundNumberList = {"none", "0", "1", "2", "3", "4", "5", "6", "7"};

                        for (int i = 0; i < soundNumberList.length; i++) {
                            if (soundNumberList[i].equalsIgnoreCase(m_soundNumber)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Sound Number")
                                .setSingleChoiceItems(soundNumberList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        m_soundNumber = soundNumberList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;

                    case 14:     // Sound Volume
                        final String[] soundVolumeList = {"none", "0", "1", "2", "3", "4", "5",
                                "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
                                "off", "min", "max"};

                        for (int i = 0; i < soundVolumeList.length; i++) {
                            if (soundVolumeList[i].equalsIgnoreCase(m_soundVolume)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Sound Volume")
                                .setSingleChoiceItems(soundVolumeList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        m_soundVolume = soundVolumeList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;

                    case 15:     //CallBack
                        final String[] cbIndexList = {"startest://"};

                        for (int i = 0; i < cbIndexList.length; i++) {
                            if (cbIndexList[i].equalsIgnoreCase(m_callBack)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Call Back")
                                .setSingleChoiceItems(cbIndexList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        m_callBack = cbIndexList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;
                    case 16:     //BlackMark
                        final String[] blackmarkIndexList = {"none", "disable", "enable", "enableAndDetectAtPowerOn"};

                        for (int i = 0; i < blackmarkIndexList.length; i++) {
                            if (blackmarkIndexList[i].equalsIgnoreCase(m_blackmark)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Black Mark")
                                .setSingleChoiceItems(blackmarkIndexList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        m_blackmark = blackmarkIndexList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;
                    case 17:     //CutType
                        final String[] cutTypeIndexList = {"none", "partial", "full", "tearbar", "nocut"};

                        for (int i =0 ;i < cutTypeIndexList.length; i++) {
                            if (cutTypeIndexList[i].equalsIgnoreCase(m_cuttype)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Cut")
                                .setSingleChoiceItems(cutTypeIndexList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        m_cuttype = cutTypeIndexList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;
                    case 18:     //Popup
                        final String[] popupIndexList = {"none", "disable", "enable"};

                        for (int i = 0; i < popupIndexList.length; i++) {
                            if (popupIndexList[i].equalsIgnoreCase(m_popup)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Popup")
                                .setSingleChoiceItems(popupIndexList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        m_popup = popupIndexList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;
                    case 19:     //HTML Receipt Design
                        for (int i = 0; i < fileList.length; i++) {
                            if (fileList[i].equalsIgnoreCase(m_htmlReceiptData)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("HTML Receipt File")
                                .setSingleChoiceItems(fileList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        m_htmlReceiptData = fileList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;
                    case 20:     //PDF Receipt Design
                        for (int i = 0; i < pdfFileList.length; i++) {
                            if (pdfFileList[i].equalsIgnoreCase(m_pdfReceiptData)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("PDF Receipt File")
                                .setSingleChoiceItems(pdfFileList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        m_pdfReceiptData = pdfFileList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;
                    case 21:     //URL Design
                        final String[] urlIndexList = {"none",
                                                       "https://www.star-m.jp/products/s_print/sdk/passprnt/sample/resource/receipt_sample.html",
                                                       "https://www.star-m.jp/products/s_print/sdk/passprnt/sample/resource/receipt_sample.pdf"};

                        for (int i = 0; i < urlIndexList.length; i++) {
                            if (urlIndexList[i].equalsIgnoreCase(m_url)) {
                                selectedIndex = i;
                                break;
                            }
                        }

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Popup")
                                .setSingleChoiceItems(urlIndexList, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        selectedIndex = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        m_url = urlIndexList[selectedIndex];
                                        refreshListInfo();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        final Uri.Builder builder = new Uri.Builder();
        builder.scheme("starpassprnt");
        builder.authority("v1");
        builder.path("/print/nopreview");

        if (!m_portname.equalsIgnoreCase("none")) {
            if (m_portname.equalsIgnoreCase("blank")) {
                builder.appendQueryParameter("port", "");
            } else {
                builder.appendQueryParameter("port", m_portname);
            }
        }
        if (!m_portSettings.equalsIgnoreCase("none")) {
            if (m_portSettings.equalsIgnoreCase("blank")) {
                builder.appendQueryParameter("settings", "");
            } else {
                builder.appendQueryParameter("settings", m_portSettings);
            }
        }
        if (!m_timeout.equalsIgnoreCase("none")) {
            if (m_timeout.equalsIgnoreCase("blank")) {
                builder.appendQueryParameter("timeout", "");
            } else {
                builder.appendQueryParameter("timeout", m_timeout);
            }
        }
        if (!m_width.equalsIgnoreCase("none")) {
            if (m_width.equalsIgnoreCase("blank")) {
                builder.appendQueryParameter("size", "");
            } else {
                builder.appendQueryParameter("size", m_width);
            }
        }
        if (!m_drawer.equalsIgnoreCase("none")) {
            if (m_drawer.equalsIgnoreCase("blank")) {
                builder.appendQueryParameter("drawer", "");
            } else if (m_drawer.startsWith("Before")) {
                builder.appendQueryParameter("drawer", "ahead");
            } else if (m_drawer.startsWith("After")) {
                builder.appendQueryParameter("drawer", "after");
            } else {
                builder.appendQueryParameter("drawer", m_drawer);
            }
        }
        if (!m_pulse.equalsIgnoreCase("none")) {
            if (m_pulse.equalsIgnoreCase("blank")) {
                builder.appendQueryParameter("drawerpulse", "");
            } else {
                builder.appendQueryParameter("drawerpulse", m_pulse);
            }
        }
        if (!m_buzzer.equalsIgnoreCase("none")) {
                builder.appendQueryParameter("buzzer",  m_buzzer);
            }
        if (!m_buzzerChannel.equalsIgnoreCase("none")) {
                builder.appendQueryParameter("buzzerchannel", m_buzzerChannel);
            }
        if (!m_buzzerRepeat.equalsIgnoreCase("none")) {
                builder.appendQueryParameter("buzzerrepeat",  m_buzzerRepeat);
            }
        if (!m_buzzerDriveTime.equalsIgnoreCase("none")) {
                builder.appendQueryParameter("buzzerdrivetime", m_buzzerDriveTime);
            }
        if (!m_buzzerDelayTime.equalsIgnoreCase("none")) {
                builder.appendQueryParameter("buzzerdelaytime",  m_buzzerDelayTime);
            }

        if (!m_sound.equalsIgnoreCase("none")) {
                builder.appendQueryParameter("sound",  m_sound);
            }
        if (!m_soundStorageArea.equalsIgnoreCase("none")) {
                builder.appendQueryParameter("soundstoragearea", m_soundStorageArea);
            }
        if (!m_soundNumber.equalsIgnoreCase("none")) {
                    builder.appendQueryParameter("soundnumber", m_soundNumber);
                }
        if (!m_soundVolume.equalsIgnoreCase("none")) {
                        builder.appendQueryParameter("soundvolume", m_soundVolume);
                    }
        if (!m_callBack.equalsIgnoreCase("none")) {
            if (m_callBack.equalsIgnoreCase("blank")) {
                builder.appendQueryParameter("back", "");
            } else {
                builder.appendQueryParameter("back", m_callBack);
            }
        }
        if (!m_blackmark.equalsIgnoreCase("none")) {
            if (m_blackmark.equalsIgnoreCase("blank")) {
                builder.appendQueryParameter("blackmark", "");
            } else {
                builder.appendQueryParameter("blackmark", m_blackmark);
            }
        }
        if (!m_cuttype.equalsIgnoreCase("none")) {
            if (m_cuttype.equalsIgnoreCase("blank")) {
                builder.appendQueryParameter("cut", "");
            } else {
                builder.appendQueryParameter("cut", m_cuttype);
            }
        }
        if (!m_popup.equalsIgnoreCase("none")) {
            if (m_popup.equalsIgnoreCase("blank")) {
                builder.appendQueryParameter("popup", "");
            } else {
                builder.appendQueryParameter("popup", m_popup);
            }
        }

        if (!m_htmlReceiptData.equalsIgnoreCase("none")) {
            if (m_htmlReceiptData.equalsIgnoreCase("blank")) {
                builder.appendQueryParameter("html", "");
            } else {
                builder.appendQueryParameter("html", String.valueOf(generatePrintData2(m_htmlReceiptData)));
            }
        }
        if (!m_pdfReceiptData.equalsIgnoreCase("none")) {
            if (m_pdfReceiptData.equalsIgnoreCase("blank")) {
                builder.appendQueryParameter("pdf", "");
            } else {
                byte[] encode = Base64.encode(generatePrintData(m_pdfReceiptData), Base64.DEFAULT);
                builder.appendQueryParameter("pdf", new String(encode));
            }
        }

        if (!m_url.equalsIgnoreCase("none")) {
            if (m_url.equalsIgnoreCase("blank")) {
                builder.appendQueryParameter("url", "");
            } else {
                builder.appendQueryParameter("url", m_url);
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Send")
                .setMessage(builder.build().toString())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(builder.build());
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

    }

    private void refreshListInfo() {
        listViewItems.clear();

        //port
        Map<String, String> map0 = new HashMap<>();
        map0.put("main", "Port Name(port=)");
        map0.put("sub", m_portname);
        listViewItems.add(map0);

        //settings
        Map<String, String> map1 = new HashMap<>();
        map1.put("main", "Port Settings(settings=)");
        map1.put("sub", m_portSettings);
        listViewItems.add(map1);

        //timeout
        Map<String, String> map2 = new HashMap<>();
        map2.put("main", "Timeout(timeout=)");
        map2.put("sub", m_timeout);
        listViewItems.add(map2);

        //size
        Map<String, String> map3 = new HashMap<>();
        map3.put("main", "Width(size=)");
        map3.put("sub", m_width);
        listViewItems.add(map3);

        //drawer
        Map<String, String> map4 = new HashMap<>();
        map4.put("main", "Drawer(drawer=)");
        map4.put("sub", m_drawer);
        listViewItems.add(map4);

        //pulse
        Map<String, String> map5 = new HashMap<>();
        map5.put("main", "Drawer Pulse(drawerpulse=)");
        map5.put("sub", m_pulse);
        listViewItems.add(map5);

        // buzzer
        Map<String, String> buzzerMap = new HashMap<>();
        buzzerMap.put("main", "Buzzer(buzzer=)");
        buzzerMap.put("sub", m_buzzer);
        listViewItems.add(buzzerMap);

        // buzzerChannel
        Map<String, String> buzzerChannelMap = new HashMap<>();
        buzzerChannelMap.put("main", "Buzzer Channel(buzzerchannel=)");
        buzzerChannelMap.put("sub", m_buzzerChannel);
        listViewItems.add(buzzerChannelMap);

        // buzzerRepeat
        Map<String, String> buzzerRepeatMap = new HashMap<>();
        buzzerRepeatMap.put("main", "Buzzer Repeat(buzzerrepeat=)");
        buzzerRepeatMap.put("sub", m_buzzerRepeat);
        listViewItems.add(buzzerRepeatMap);

        // buzzerDriveTime
        Map<String, String> buzzerDriveTimeMap = new HashMap<>();
        buzzerDriveTimeMap.put("main", "Buzzer Drive Time(buzzerdrivetime=)");
        buzzerDriveTimeMap.put("sub", m_buzzerDriveTime);
        listViewItems.add(buzzerDriveTimeMap);

        // buzzerDelayTime
        Map<String, String> buzzerDelayTimeMap = new HashMap<>();
        buzzerDelayTimeMap.put("main", "Buzzer Delay Time(buzzerdelaytime=)");
        buzzerDelayTimeMap.put("sub", m_buzzerDelayTime);
        listViewItems.add(buzzerDelayTimeMap);

        // sound
        Map<String, String> soundMap = new HashMap<>();
        soundMap.put("main", "Sound(sound=)");
        soundMap.put("sub", m_sound);
        listViewItems.add(soundMap);

        // soundStorageArea
        Map<String, String> soundStorageAreaMap = new HashMap<>();
        soundStorageAreaMap.put("main", "Sound Storage Area(soundstoragearea=)");
        soundStorageAreaMap.put("sub", m_soundStorageArea);
        listViewItems.add(soundStorageAreaMap);

        // soundNumber
        Map<String, String> soundNumberMap = new HashMap<>();
        soundNumberMap.put("main", "Sound Number(soundnumber=)");
        soundNumberMap.put("sub", m_soundNumber);
        listViewItems.add(soundNumberMap);

        // soundVolume
        Map<String, String> soundVolumeMap = new HashMap<>();
        soundVolumeMap.put("main", "Sound Volume(soundvolume=)");
        soundVolumeMap.put("sub", m_soundVolume);
        listViewItems.add(soundVolumeMap);

        //callback
        Map<String, String> map6 = new HashMap<>();
        map6.put("main", "Call Back(back=)");
        map6.put("sub", m_callBack);
        listViewItems.add(map6);

        //black mark
        Map<String, String> map7 = new HashMap<>();
        map7.put("main", "Black Mark(blackmark=)");
        map7.put("sub", m_blackmark);
        listViewItems.add(map7);

        //cuttype
        Map<String, String> map8 = new HashMap<>();
        map8.put("main", "Cut Type(cut=)");
        map8.put("sub", m_cuttype);
        listViewItems.add(map8);

        //popup
        Map<String, String> map9 = new HashMap<>();
        map9.put("main", "Popup(popup=)");
        map9.put("sub", m_popup);
        listViewItems.add(map9);

        //html Receipt data
        Map<String, String> map10 = new HashMap<>();
        map10.put("main", "Receipt(html=)");
        map10.put("sub", m_htmlReceiptData);
        listViewItems.add(map10);

        //pdf Receipt data
        Map<String, String> map11 = new HashMap<>();
        map11.put("main", "Receipt(pdf=)");
        map11.put("sub", m_pdfReceiptData);
        listViewItems.add(map11);

        //url
        Map<String, String> map12 = new HashMap<>();
        map12.put("main", "Receipt(url=)");
        map12.put("sub", m_url);
        listViewItems.add(map12);

        adapter.notifyDataSetChanged();
    }

    private byte[] generatePrintData(String file) {
        InputStream is = null;
        byte[] readBytes = null;

        try {
            try {
                // open a file on assets folder
                is = this.getAssets().open(file);

                readBytes = new byte[is.available()];
                is.read(readBytes);

            } finally {
                if (is != null) is.close();
            }
        } catch (Exception e){
            // Do nothing
        }
        return readBytes;
    }

    private String generatePrintData2(String file) {
        String data = "";

        InputStream is = null;
        BufferedReader br = null;

        try {
            try {
                // open a file on assets folder
                is = this.getAssets().open(file);
                br = new BufferedReader(new InputStreamReader(is));

                String str;

                // read data line by line
                while ((str = br.readLine()) != null) {
                    data += str;
                }
            } finally {
                if (is != null) is.close();
                if (br != null) br.close();
            }
        } catch (Exception e){
            // Do nothing
        }

        return data;
    }

    private void OutputReturnValue() {
        Intent intent = getIntent();

        if (intent.getScheme() == null)
            return;

        // If you need the part of query in scheme,
        // you can use "intent.getData().getQuery()"
        // instead of "intent.getDataString()".
        new AlertDialog.Builder(this)
                .setTitle("Result")
                .setMessage(intent.getDataString())
                .setPositiveButton("OK", null)
                .show();
    }
}
