# RedDotPrompt
移动APP项目中,页面的层次一般都不会只有一层.有时第二层页面有消息要提示了通过小红点的方式在第一层页面中提示用户是比较好的,但是页面之间的红点是没有关系的,要逐个更新,很麻烦."RedDotPrompt"可以做到有消息了只要更新最子级的控件,父级控件会自动跟着更新.只要更新真的有消息的页面的一个控件即可.

## 效果演示
![image](images/red_dot.gif)

## 开始使用
### 在需要小红点提示的布局文件中加入控件.tag属性的格式是:"${自己ID}#S{父级ID}".如:"1#-1",1代表自己的ID,-1代表没有父级.格式一定要正确,最好使用int类型.tag属性一定不能为空.
![image](images/xml_code.png)

### 在你的Application对象里初始化你项目中的所有小红点(RedDotTextView)的tag属性.这个主要是生成红点之间的层级关系,如果你的项目中红点的层级不超过2层可以不初始化.
![image](images/app_code.png)

### 更新小红点的消息可以调用静态方法RedDotUtil.updateMessage();
![image](images/activity_code.png)

## 如果感觉帮助到了你.请右上角给个赞吧!在使用过程中遇到什么BUG欢迎给我反馈.
邮箱:panxiong@outlook.com
