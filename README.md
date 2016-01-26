## WhatsInput

WhatsInput，是一款输入法，通过 WIFI，使你的PC键盘向手机输入内容。

1. 漂亮的 ICON
2. 简单的输入界面

希望 WhatsInput 可以在某个情景中，帮助你一些。

1. 使用一些移动互联网的 IM 类应用时，假如：陌陌，WhatsApp，KiK 等
2. 轻量级短信快速的输入方案
3. 安卓应用测试时，需要内容输入
4. ....

### 使用方法：

1. 在设置中启用 WhatsInput
2. 在需要使用时切换到 WhatsInput 输入法，这时你会看到这样的地址： <http://192.168.1.100:6688>
3. 在浏览器中打开
5. 支持 Enter 和 TAB 键

下载地址：<https://play.google.com/store/apps/details?id=com.buscode.whatsinput>


### 手机---> 前端

1.  开始编辑

```
{"text":"sssss", "type":"InputStart"}
```

- type 消息类型 InputStart
- text 开始编辑时的内容

2.  编辑结束

```
 {"type": "InputFinish" }
```

- type 消息类型 InputFinish


3. 编辑框内容变化

```
{"type": "InputChange", text: ""}
```

- type 消息类型
- text 变化后的编辑框内容。	
