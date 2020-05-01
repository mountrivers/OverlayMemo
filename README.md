# OverlayMemo
## 플레이 스토어 링크 
https://play.google.com/store/apps/details?id=com.sanha.overlaymemo

## 개발 목적 
 스마트폰으로 무언가를 하다보면, 두개의 어플을 번갈아 가면서 봐야 할 때가 있습니다. 예를들어, 게임을 하다가 공략을 보기 위해 다른 어플로 들어갔다, 다시 돌아오고, 이런것을 반복 한다던지 이럴때 항상 위에 떠있는 메모가 있으면 어떨까 하는 생각이 첫번째였고, 항상 자주보는 스마트폰에서 맨 위에 메모를 띄워 놓는다면 깜빡 하는 일도 줄일 수 있다고 생각하여 만들게 되었습니다. 
 
## 개발 인원 
### 개발자 
이산하 (tksgk77@gmail.com)
<br/>
### UX UI 디자인
황정주 (jjayhwang@gmail.com)
<br/>

## 개발 기간 
 2020.02 ~ 지속 업데이트중
 

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

<br/><br/>
# 주요 기능 코드


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

### 메모 컨트롤 
- 글자크기/ 창크기 => getSelectedManager 을 통해 Manager 에서 실행 

- 색상변경 => opensource 사용 Github https://github.com/yukuku/ambilwarna

## Manager class
```
public class Manager {

    public int getId() {
        return id;
    }

    public int id;
    public SharedPreferences spPref;
    public SharedPreferences.Editor spEditor;
    public Resources r;
    public Manager(int a){
        id = a;
    }

    public void loadSizes(Context context) {
        spPref = context.getSharedPreferences("spPref", MODE_PRIVATE);
        spEditor = spPref.edit();
        r = context.getResources();
        mSize = spPref.getInt("size"+id, 14);
        mWidth = spPref.getInt("width"+id, 150);
    }
    public void setMSize(){
        spEditor.putInt("size"+id,mSize);
        spEditor.commit();
    }
    public void setMWidth(){
        spEditor.putInt("width"+id,mWidth);
        spEditor.commit();
    }

    public int getmSize() {
        return mSize;
    }

    public int getPixel(){
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, mWidth, r.getDisplayMetrics()));
    }
    public void resizeSize(int a){
        if(a==0)
            mSize++;
        else if( a== 1 && mSize > 1)
            mSize--;
    }
    public void resizeWidth(int a){
        if( a == 0)
            mWidth+=10;
        else if( a == 1 && mWidth > 140)
            mWidth-= 10;
    }
    public void loadColor(Context context) {
        r = context.getResources();
        color = spPref.getInt("color"+id, 0xFFFFFFFF);
    }
    public int getBackGroundColor(){
        return color;
    }
    public void setBackGroundColor(int getColor){
        color = getColor;
        spEditor.putInt("color"+id,color);
        spEditor.commit();
    }
}
```
 -pixel <=> DIP 변환 ( edittext 는 DIP 기준으로 하여야 줄바꿈이 올바로 됨)
 
 
 ## DB ( room 사용 )
 ### MemoDao
```
@Dao
public interface MemoDao {
    @Query("SELECT * FROM Memo")
    List<Memo> getAll();

    @Query("SELECT * FROM Memo WHERE id = :ids")
    Memo getA(int ids) ;

    @Query("SELECT COUNT(*) FROM Memo")
    int getC();


    @Insert
    void insert(Memo memo);

    @Update
    void update(Memo memo);

    @Delete
    void delete(Memo memo);
}
```
### Memo
```
@Entity
public class Memo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private  String content ="";

    public Memo(String content){
        this.content= content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }

}
```

## MyService
### onStartCommand(Override)
```
public int onStartCommand(Intent intent, int flags, int startId) {
        changeSize(intent.getExtras().getInt("textsize", 14));
        changeWidth(intent.getIntExtra("textwidth", 150));
        colorChange(intent.getIntExtra("color",0xFFFFFFFF));
        return super.onStartCommand(intent, flags, startId);
    }
```
- MainActivity 에서 받아온 intent로 창 크기, 색상 설정

### loadText
```
public void loadText() {
        setDB();

        floatingText = (EditText) mView.findViewById(R.id.textView);
        floatingText.setText(memo.toString());
        floatingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((params.flags & WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE) != 0)
                    params.flags -= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                wm.updateViewLayout(mView, params);
            }
        });

        /*      텍스트 롱 클릭시 클립보드 ( 복사, 붙여넣기, 지우기 열기 ) */
        floatingText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), v);//v는 클릭된 뷰를 의미
                popup.getMenuInflater().inflate(R.menu.contextual_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.paste:
                                temp = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
                                startIndex = Math.max(floatingText.getSelectionStart(), 0);
                                endIndex = Math.max(floatingText.getSelectionEnd(), 0);
                                floatingText.getText().replace(Math.min(startIndex, endIndex), Math.max(startIndex, endIndex), temp);
                                break;

                            case R.id.copy:
                                temp = floatingText.getText().toString();
                                startIndex = floatingText.getSelectionStart();
                                endIndex = floatingText.getSelectionEnd();
                                temp = temp.substring(startIndex, endIndex);
                                ClipData clip = ClipData.newPlainText("clip", temp);
                                clipboardManager.setPrimaryClip(clip);
                                break;

                            case R.id.delete:
                                startIndex = Math.max(floatingText.getSelectionStart(), 0);
                                endIndex = Math.max(floatingText.getSelectionEnd(), 0);
                                floatingText.getText().replace(Math.min(startIndex, endIndex), Math.max(startIndex, endIndex), "");
                                break;

                            default:
                                break;
                        }
                        return false;
                    }
                });
                popup.show();//Popup Menu 보이기
                return false;
            }
        });
    }
```
- setDB()는 DB불러오는 것으로, S1,S2,S3 클래스에서 상속받아, 오버라이드 하여 사용

- DB에서 받은 텍스트를 불러와 Edittext 에 띄워줌 

- EditText창 롱터치시, 직접만든 클립보드 열리도록 사용. ( Service 에서 롱클릭시 기본 클립보드 불러오지 못함 )

## button ( 창 축소 / 확대 ) 
```
buttonZoomingMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingText.setVisibility(v.GONE);
                buttonExit.setVisibility(v.GONE);
                buttonSave.setVisibility(v.GONE);
                buttonPaste.setVisibility(View.GONE);
                buttonZoomingMinus.setVisibility(View.GONE);
                buttonZoomingPlus.setVisibility(View.VISIBLE);
            }
        });
buttonZoomingPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingText.setVisibility(v.VISIBLE);
                buttonExit.setVisibility(v.VISIBLE);
                buttonSave.setVisibility(v.VISIBLE);
                buttonPaste.setVisibility(View.VISIBLE);
                buttonZoomingMinus.setVisibility(View.VISIBLE);
                buttonZoomingPlus.setVisibility(View.GONE);
            }
        });
```
모든 뷰들을 안보이도록 숨겨서 창 축소

## assignView
```
protected  void assginView(){
        inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        params = new WindowManager.LayoutParams(
                /*ViewGroup.LayoutParams.MATCH_PARENT*/ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT < 26 ?
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT :
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        attachView();

        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                if (ev.getAction() == MotionEvent.ACTION_OUTSIDE) {

                    if ((params.flags & WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE) == 0)
                        params.flags += WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                    wm.updateViewLayout(mView, params);
                } else {
                    switch (ev.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mStartingX = ev.getRawX();
                            mStartingY = ev.getRawY();

                            mWidgetStartingX = params.x;
                            mWidgetStartingY = params.y;
                            return false;
                        case MotionEvent.ACTION_MOVE:
                            float deltaX = mStartingX - ev.getRawX();
                            float deltaY = mStartingY - ev.getRawY();
                            params.x = (int) (mWidgetStartingX - deltaX);
                            params.y = (int) (mWidgetStartingY - deltaY);
                            wm.updateViewLayout(mView, params);
                            return true;
                    }

                }

                return false;
            }
        });

        wm.addView(mView, params);
        backGround = (LinearLayout) mView.findViewById(R.id.service_background);
        setXY();
    }
```
- param 에서, sdk 버전 확인하여 26 아래면 TYPE_SYSTEM_ALERT 사용, 그 이후 버전은 TYPE_APPICATION_OVERLAY 사용 

- 터치리스너를 통하여 창 이동 

## S1 / S2 / S3 
```
public class S1 extends MyService {
    public static S1 s1 ; // 정의 추가

    @Override
    public void setBasic() {
        s1 = this;
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    }
    @Override
    protected void setDB() {
        db = Room.databaseBuilder(this,
                AppDatabase.class, "memo-db")
                .allowMainThreadQueries()
                .build();
        while(db.todoDao().getC() <= 3) {
            db.todoDao().insert(new Memo(""));
        }
        memo = db.todoDao().getA(1);
    }

    @Override
    protected void attachView(){
        mView = inflate.inflate(R.layout.view_in_service, null);
    }

    @Override
    protected  void setXY(){
        params.x = -100;
        params.y = -20;
        wm.updateViewLayout(mView,params);
    }
}
```
### setDB 
 - 몇번째 DB를 연결 할 지 선택 
### attachView
 - 몇번 XML과 연결 할 지 선택
### setXY
 - 초기 시작 위치 설정 (3개 한번에 켰을때 겹쳐보이지 않도록 ) 
 
### foreground 
포그라운드 서비스 어플이라는 것을 고지 하기 위해, 노티바를 만들어

포그라운드 서비스로 제작. 

포그라운드 서비스가 아니면, 화면 꺼둘 시 자동으로 종료 되어버리는 불상사가 생김



---------------------------------


[ 메모 어플 제작 ]
- 활동 진행 기간 : 2020.02 ~ 지속 업데이트중 
- 주요 내용 : 화면 최상단에 위치하는 메모 애플리케이션 개발
- 사용 언어 : Android Studio
- 사용 오픈 소스 : room 
- URL(GIT) : https://github.com/mountrivers/OverlayMemo
- TAG : #Users Rule, #Perfect Details, 1% Problem-finding, 99% Solution-making

 저의 다양한 취미생활 중 하나는, 모바일 게임을 하는 것입니다. 모바일 게임 중에서도, 전략이 필요한 게임을 많이 하는 편인데, 전략에 관하여 직접 연구 하기도 하고, 다른 사람의 공략을 보고 플레이 하기도 합니다. 그러다 보니, 모바일 게임을 진행하면서도, 계속해서 최근 사용한 앱 버튼을 눌러서, 웹을 봤다가, 게임을 왔다가 계속 왕복을 하는 일이 생기고, 상당히 귀찮음을 느꼈습니다. 그러다 보니, 항상 최상단에 떠 있는 메모 앱이 있으면 어떨까 하는 생각에 개발을 시작하게 되었습니다. 
 
 앱을 만들어 나가다 보니 다양한 문제점이 있었습니다. API의 버전 차이에 따라 되는 것이 있고, 안되는 것이 있어서 개발에 난항을 겪었습니다. overlay 권한을 받는 것부터 시작해서, 메모를 표현할 LayoutParam 또한 API 버전에 따라 차이가 있었습니다. 최대한 공식 문서를 참조하여, 버전별로 문제없이 호환되도록 만들어 나갔습니다.
 
 그 외에 가장 힘들었던 점은, 내가 딱 필요한 기능을 구현하기 위한 자료를 찾기 어렵다는 점이었습니다. 개발자 페이지에서, 해당 기능의 문서는 기본 사용법만 있고, 이것을 응용에 응용으로 사용해야 하다 보니, 원하는 방법을 찾기 쉽지 않았습니다. 그래서, 해결하기 힘든 이슈가 있으면, 비슷하지만 다른 이슈들을 해결했던 사례들을 찾아보고, 목록을 작성하고, 조금씩 수정, 변경, 응용하여 이슈들을 해결 해 나갔습니다. 
 그렇게 앱의 필요 기능들을 늘려가며, 완성도를 높여가던 중, 도저히 혼자서는 해결되지 않는 것이 있었습니다. 바로 UI/UX가 별로여서 사용자에게 만족감을 줄 수 있을지가 미지수였던 것입니다. 이것을 어떻게 해결해야 할까 고민하던 도중, 혼자서 만족감을 주기는 힘들겠다고 생각하여, 네이버 카페인 스펙업 에서, UI/UX 진로를 희망하는 사람을 찾아서, 함께 협업하여 앱을 만들자고 제안을 했습니다. 수익도 나지 않고, 개인적으로 하던 프로젝트였기 때문에, 수익을 제공할 수 없었지만, 고맙게도 그분께서도 앱 제작에 참여해 보고 싶다고 하셨고, 그 덕에 같이 협업하게 되었습니다. UI를 만드는 것에서부터 시작해서, UX 또한 향상하기 위하여 많은 이야기를 주고받았습니다. 그렇게 하고 난 후에야, 드디어 앱 같은 앱이 어느 정도 완성이 되었습니다. 
 
 플레이스토어에 등록하고 운영을 하는데, 다른 애플리케이션들은 평점란에, 불편한 점이라던가, 개선이 필요한 점이 올라오는 것을 자주 봐 왔기에, 저의 앱에도 사용자들이 개선 사항을 적어 주지 않을까 했지만, 아무래도 사용자가 적다 보니 좀처럼 평점과 후기가 올라오는 일이 없었습니다. 그래서 이대로는 앱의 발전이 힘들다고 생각하였고, 주변 지인들에게 앱을 알리고, 피드백을 요구해서 앱을 업데이트 해 나갔습니다. 피드백과 피드백이 상반돼서 결단이 필요한 순간도 있었지만, 최대한 절충안을 찾아서 앱의 완성도를 높여 가고 있습니다.
  
