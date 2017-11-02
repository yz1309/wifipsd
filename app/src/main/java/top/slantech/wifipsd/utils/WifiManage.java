package top.slantech.wifipsd.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.slantech.wifipsd.bean.Wifis;
import top.slantech.yzlibrary.utils.StringUtils;
import top.slantech.yzlibrary.utils.ULog;

/**
 * Created by admin on 2016/7/10 0010.
 */
public class WifiManage {

    /**
     * 此种方式漏掉ssid=xx此种情况
     * ssid="xx" 带双引号和不带双引号 暂时未用
     *
     * @return
     * @throws Exception
     */
    public List<Wifis> Read() throws Exception {
        List<Wifis> wifiInfos = new ArrayList<Wifis>();

        Process process = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        StringBuffer wifiConf = new StringBuffer();
        try {
            process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataInputStream = new DataInputStream(process.getInputStream());
            dataOutputStream.writeBytes("cat /data/misc/wifi/*.conf\n");
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            InputStreamReader inputStreamReader = new InputStreamReader(dataInputStream, "UTF-8");
            //InputStreamReader inputStreamReader = new InputStreamReader(dataInputStream, "GB2312");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                wifiConf.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            process.waitFor();
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                process.destroy();
            } catch (Exception e) {
                throw e;
            }
        }

        Pattern network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL);
        Matcher networkMatcher = network.matcher(wifiConf.toString());
        while (networkMatcher.find()) {
            String networkBlock = networkMatcher.group();

            // networkBlock {	ssid="yz1309"	psk="yz1309@163.com"	key_mgmt=WPA-PSK	priority=1}
            // 中文  network={	ssid=e58da1e789b9	psk="yz1309com"	key_mgmt=WPA-PSK	priority=13	frequency=2412	autojoin=1	usable_internet=0	skip_internet_check=0}

            Pattern ssidPattern = Pattern.compile("ssid=\"([^\"]+)\"");
            Pattern pskPattern = Pattern.compile("psk=\"([^\"]+)\"");
            Pattern zwssid = Pattern.compile("ssid=([^\"]+)");
            Matcher ssidMatcher = ssidPattern.matcher(networkBlock);
            Matcher zsssidMatcher = zwssid.matcher(networkBlock);

            String ssidStr = "";
            String pskStr = "";
            if (ssidMatcher.find()) {
                ssidStr = ssidMatcher.group(1);
                Matcher pskMatcher = pskPattern.matcher(networkBlock);
                if (pskMatcher.find()) {
                    pskStr = pskMatcher.group(1);

                } else {
                    pskStr = "无密码";
                }
            } else if (zsssidMatcher.find()) {
                ssidStr = zsssidMatcher.group(1);
                Matcher pskMatcher = pskPattern.matcher(networkBlock);
                if (pskMatcher.find()) {
                    pskStr = pskMatcher.group(1);

                } else {
                    pskStr = "无密码";
                }
            }
            Wifis wifiInfo = new Wifis();
            wifiInfo.setSsid(ssidStr);
            wifiInfo.setPassword(pskStr);
            wifiInfo.setRssi(-90);
            wifiInfo.setChecked(false);
            wifiInfos.add(wifiInfo);
        }
        return wifiInfos;
    }

    public List<Wifis> getWifiList() {
        List<String> strings;
        List<Wifis> wifiList;
        List<String> listNetwork, listID, listPass, listPriority, listKey_mgmt, listFrequency;
        strings = new ArrayList<>();
        wifiList = new ArrayList<>();
        strings.add("cd data/" + ShellUtils.COMMAND_LINE_END);
        strings.add("cd misc/" + ShellUtils.COMMAND_LINE_END);
        strings.add("cd wifi/" + ShellUtils.COMMAND_LINE_END);
        strings.add("ls" + ShellUtils.COMMAND_LINE_END);
        strings.add("cat wpa_supplicant.conf" + ShellUtils.COMMAND_LINE_END);
        ShellUtils.CommandResult result = ShellUtils.execCommand(strings, true, true);
        //获取返回的结果
        String wifis = result.successMsg;
        wifis = wifis.replaceAll("key_mgmt=NONE", "psk=\"无密码\" key_mgmt=NONE\tpriority=-1").
                replace("}", "\t").
                replace("scan_ssid=1","");
        ULog.e("wifilist--" + wifis);
        listNetwork = new ArrayList<>();
        listID = new ArrayList<>();
        listPass = new ArrayList<>();
        listPriority = new ArrayList<>();
        listKey_mgmt = new ArrayList<>();
        listFrequency = new ArrayList<>();

        int passNum = 0, passReNum = -1;
        //获取密码
        String rexPass = "psk=\"[^\"]+\"";
        Pattern paPass = Pattern.compile(rexPass);
        Matcher maPass = paPass.matcher(wifis);
        while (maPass.find()) {
            passNum++;
            String temp = maPass.group();
            if (!StringUtils.isEmpty(temp) && temp.contains("无密码")) {
                // 记录没有密码的索引，为下边获取数据将其排除掉
                passReNum = passNum;
            }
            if (!StringUtils.isEmpty(temp) && !temp.contains("无密码"))
                listPass.add(maPass.group());
        }

        int idNum = 0;
        //获取用户名
        String rexId = "ssid=[\\S]+[\\s]";
        Pattern paId = Pattern.compile(rexId);
        Matcher maId = paId.matcher(wifis);

        while (maId.find()) {
            idNum++;
            if (passReNum > 0 && idNum == passReNum) {
                ULog.e("passReNum="+passReNum+",idNum="+idNum);
            } else if (idNum <= passNum)
                listID.add(maId.group());
        }
        int priorityNum = 0;
        //获取优先级
        String rexPriority = "priority=[\\S]+[\\s]";
        Pattern paPriority = Pattern.compile(rexPriority);
        Matcher maPriority = paPriority.matcher(wifis);
        while (maPriority.find()) {
            priorityNum++;
            if (passReNum > 0 && priorityNum == passReNum) {

            } else if (idNum <= passNum)
                listPriority.add(maPriority.group());
        }
        int keymgmtNum = 0;
        //获取加密方式
        String rexKey_mgmt = "key_mgmt=[\\S]+[\\s]";
        Pattern paKey_mgmt = Pattern.compile(rexKey_mgmt);
        Matcher maKey_mgmt = paKey_mgmt.matcher(wifis);
        while (maKey_mgmt.find()) {
            keymgmtNum++;
            if (passReNum > 0 && keymgmtNum == passReNum) {

            } else
                listKey_mgmt.add(maKey_mgmt.group());
        }

        int frequencyNum = 0;
        //获取频率
        String rexFrequency = "frequency=[\\S]+[\\s]";
        Pattern paFrequency = Pattern.compile(rexFrequency);
        Matcher maFrequency = paFrequency.matcher(wifis);
        while (maFrequency.find()) {
            frequencyNum = 0;
            if (passReNum > 0 && frequencyNum == passReNum) {

            } else if (idNum <= passNum)
                listFrequency.add(maFrequency.group());
        }
        if (listFrequency.size() == 0) {
            for (int i = 0; i < listID.size(); i++) {
                Random r = new Random();
                // 10 - 70
                listFrequency.add("24" + (10 + r.nextInt(60)));
            }
        }

        if (listID.size() > 0 && listPass.size() > 0 && listFrequency.size() > 0 && listPriority.size() > 0 && listKey_mgmt.size() > 0)
            for (int i = 0; i < listID.size(); i++) {
                // 包含双引号
                String mId = listID.get(i);
                String mPass = listPass.get(i);
                String mPriority = listPriority.get(i);
                String mKey_mgmt = listKey_mgmt.get(i);
                String mFrequency = listFrequency.get(i);
                if (mId.contains("\"")) {
                    mId = mId.substring(6, mId.length() - 2);
                } else {
                    // 十六进制转换成字符串
                    try {
                        mId = mId.substring(5, mId.length() - 1);
                        mId = HexUtils.decode(mId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                mPass = mPass.substring(5, mPass.length() - 1);
                Wifis item = new Wifis();
                item.setSsid(mId);
                item.setPassword(mPass);
                item.setRssi(-90);
                item.setPriority(mPriority.replace("priority=", ""));
                item.setKey_mgmt(mKey_mgmt.replace("key_mgmt=", ""));
                item.setFrequency(mFrequency.replace("frequency=", ""));
                item.setChecked(false);
                wifiList.add(item);
            }

        return wifiList;
    }


}