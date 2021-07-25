package com.kltn.hookdemo;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.kltn.hookdemo.hooking.ActivityContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import de.robv.android.xposed.SELinuxHelper;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.services.BaseService;

public class FileUtils {
    private static final String DATA_DIRECTORY = "/data/data";
    private static final String SCAN_FLAG_FILENAME = "files";
    private static final String RESULTS_FILENAME = "test.txt";
    private static boolean CHECK_FLAG = false;

    public static boolean shouldScan(String packageName) {
        BaseService baseService = SELinuxHelper.getAppDataFileService();
        return baseService.checkFileExists(DATA_DIRECTORY + "/" + packageName + "/" + SCAN_FLAG_FILENAME);
    }

    public static boolean saveStringToResultsFile(String packageName, String inputURL) {


        try {
            //Context mContext = ActivityContext.getCurrentActivity().getApplicationContext();
            File resultsFile = new File(DATA_DIRECTORY + "/" + packageName + "/" + SCAN_FLAG_FILENAME + "/" + RESULTS_FILENAME);
            Log.e("KLTN2021: ", "Access: " + DATA_DIRECTORY + "/" + packageName + "/" + SCAN_FLAG_FILENAME + "/" + RESULTS_FILENAME);

            //Create results file if not present
            if (!resultsFile.exists()) {
                Log.e("KLTN2021: ", "Access Failed");
                boolean created = resultsFile.createNewFile();
                if (!created) {
                    XposedBridge.log("Failed to create results file. Path: " + resultsFile.getAbsolutePath());
                    //return false;
                }
            }

            //Read file content into a String
            FileReader fr = new FileReader(resultsFile);
            BufferedReader br = new BufferedReader(fr);

            StringBuilder fileContentBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                fileContentBuilder.append(line);
                Log.e("KLTN2021", "line: " + line);
            }

            fr.close();
            br.close();

            String fileContent = fileContentBuilder.toString();
            //Log.e("KLTN2021: ", fileContent);
            //Skip if the file already contains the string
            if (fileContent.contains(inputURL)) {
               Log.e("KLTN2021", "Doc va phat hien duoc du lieu nhay cam");
               CHECK_FLAG = true;
                //Append url to the file
                //FileWriter fw = new FileWriter(resultsFile, true);
                //BufferedWriter bw = new BufferedWriter(fw);
                //bw.write(stringToWrite + "\n");
                //bw.close();
            }

        } catch (IOException e) {
            XposedBridge.log(e.getMessage());
        }
        return CHECK_FLAG;
    }
}
