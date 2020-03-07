# OverlayMemo
## 플레이 스토어 링크 
https://play.google.com/store/apps/details?id=com.sanha.overlaymemo

## 개발자 
이산하 (tksgk77@gmail.com)
<br/>
## UX UI 디자인
황정주 (jjayhwang@gmail.com)
<br/>
## 실행 화면
![Screenshot_20200307-212637](https://user-images.githubusercontent.com/36880919/76143400-7b7a0800-60ba-11ea-9395-4083771be854.png)|![Screenshot_20200307-212658](https://user-images.githubusercontent.com/36880919/76143401-7ddc6200-60ba-11ea-9f1f-297152b1d892.png)
--- | --- |
<br/><br/>
## DETAILS
<br/><br/>
### 메모시작

![메모시작](https://user-images.githubusercontent.com/36880919/76143496-46ba8080-60bb-11ea-9061-807301b6821b.jpg)

최대 3개의 메모를 띄울 수 있음  
<br/><br/><br/>

### 메모창 컨트롤
![메모창컨트롤](https://user-images.githubusercontent.com/36880919/76143495-45895380-60bb-11ea-9e58-ffbd9b023eeb.jpg)

메모창의 글자 크기, 창의 가로 크기를 조절 가능하며, 배경색 또한 변경 가능 한 컨트롤 창  
<br/><br/><br/>
### 메모 내부 컨트롤
![메모컨트롤](https://user-images.githubusercontent.com/36880919/76143498-46ba8080-60bb-11ea-860d-6dc9001eea41.jpg)

드래그로 메모창 위치 이동 / 축소-되돌리기 / 붙여넣기 / 저장 / 종료 버튼 제공  
<br/><br/><br/>
### 축소모드

![축소모드](https://user-images.githubusercontent.com/36880919/76143581-f263d080-60bb-11ea-80c0-c9d34448e232.png)

## MainActivity
### 메모 선택 ( getSelectedManager / getSelectedService ) 
```
 public Manager getSelectedManager(){
        RadioButton rd = (RadioButton) findViewById(radiogroup.getCheckedRadioButtonId());
        switch (rd.getText().toString()){
            case "1":
                return m1;
            case "2":
                return m2;
            case "3":
                return m3;
        }
        return m1;
    }
```
```
public MyService getSelectedService(){
        RadioButton rd = (RadioButton) findViewById(radiogroup.getCheckedRadioButtonId());
        switch (rd.getText().toString()){
            case "1":
                return s1;
            case "2":
                return s2;
            case "3":
                return s3;
        }
        return s1;
    }
```
### 메모창 열기 (checkPermision -> startact)
```
public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // 마시멜로우 이상일 경우
            if (!Settings.canDrawOverlays(this)) {              // 체크
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            } else {
                startAct();
            }
        } else {
            startAct();
        }
    }
```
버전에 따라 권한 설정

```
 private void startAct() {
        switch (getSelectedManager().getId()){
            case 1:
                serviceIntent = new Intent(MainActivity.this, S1.class);
                break;
            case 2:
                serviceIntent = new Intent(MainActivity.this, S2.class);
                break;
            case 3:
                serviceIntent = new Intent(MainActivity.this, S3.class);
                break;
            default:
                break;
        }

        serviceIntent.putExtra("textsize", getSelectedManager().getmSize());
        serviceIntent.putExtra("textwidth", getSelectedManager().getPixel());
        serviceIntent.putExtra("color",getSelectedManager().getBackGroundColor());

        
        startService(serviceIntent);
    }
```
체크된 메모 실행

