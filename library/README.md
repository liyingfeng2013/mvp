# **框架说明**
## **app 主程序**
#### **manager / UserManager 使用说明：**
      初始化:
          程序入口处（Application的onCreate方法中）调用 UserManager.getInstance().init(this)
          
      保存或更新用户信息:
          UserManager.getInstance().save(userModel)
          
      退出登陆:
          UserManager.getInstance().logout()
      
      Tip:保存或更新用户信息 和 退出登陆 都会触发父类BaseActivity、BaseFragment中的onEventMainThread(LoginEvent event)事件

#### **widget / LoadMoreRecyclerView（自动加载更多）使用说明：**
      1.布局文件(.xml)中添加 LoadMoreRecyclerView
      2.定义对应的 adapter类，并继承LoadMoreRecyclerView.LoadMoreAdapter 并实现其中的方法
      3.网络请求结束后调用 LoadMoreRecyclerView.onComplete() 重置自动加载更多状态

## **library 核心依赖库**
#### **picture / Picture（拍照和相册图片选择）使用说明**
###### _1.在AndroidManifest.xml中添加_
        <!--照片选择页面-->
        <activity
            android:name="com.up72.library.picture.PictureActivity"
            android:screenOrientation="portrait"/>
        <!--图片裁剪-->
        <activity
            android:name="com.up72.library.crop.CropImageActivity"
            android:screenOrientation="portrait"/>
###### _2.打开图片选择页面_
        Picture.of(this).crop(true).start();
###### _3.在Activity中接收_
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case Picture.REQUEST_CODE:
                        if (data != null && data.getExtras() != null && data.getExtras().containsKey(Picture.IMAGE_PATH)) {
                            String path = data.getExtras().getString(Picture.IMAGE_PATH, "");
                            if (path != null && path.length() > 0) {
                                // TODO do something  path为图片路径
                            }
                        }
                        break;
                }
            }
        }
#### **widget / BannerView（轮播图控件）使用说明**
       在xml文件中添加代码_
           <com.up72.library.widget.BannerView
               android:layout_width="match_parent"
               android:layout_height="match_parent"/>
       通过BannerView.setDate(new String[])设置数据
       通过BannerView.getCurrentPosition() 获取当前点击的位置
#### **widget / CountdownTextView（倒计时控件）使用说明**
       在xml文件中添加代码_
        <com.up72.library.widget.CountdownTextView
            android:id="@+id/btn1"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_margin="@dimen/space_8"
            android:gravity="center"
            android:text="获取验证码"
            android:textColor="@color/red_900"
            android:textSize="@dimen/text_size_16"
            app:countdownTextStart="重新发送倒计时"
            app:key="btn1"
            app:maxTime="10"/>
       通过CountdownTextView.startTime()开始倒计时
       ——————————————————END————————————————————————
       tip:
              <declare-styleable name="CountdownTextView">
                  <!--倒计时按钮的唯一标识，如果有多个倒计时按钮，需指定KEY-->
                  <attr name="key" format="string"/>
                  <!--倒计时最大时间，单位秒-->
                  <attr name="maxTime" format="integer"/>
                  <!--倒计时显示的文字前半部分-->
                  <attr name="countdownTextStart" format="string"/>
                  <!--倒计时显示的文字后半部分-->
                  <attr name="countdownTextEnd" format="string"/>
              </declare-styleable>