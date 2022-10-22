> 电子邮件需要在邮件客户端和邮件服务器之间，以及两个邮件服务器之间进行传递，就必须遵循一定的规则，这些规则就是邮件传输协议。

# 一、协议简介

1、SMTP协议
    全称是"Simple Mail Transfer Protocol"，即简单邮件传输协议，用于邮件发送。
    SMTP 认证，简单地说就是要求必须在提供了账户名和密码之后才可以登录 SMTP 服务器。

2、POP3协议
    (Post Office Protocol 3)协议允许电子邮件客户端下载服务器上的邮件。
    但是在客户端的操作(如移动邮件、标记已读等)，不会反馈到服务器上，
    比如通过客户端收取了邮箱中的3封邮件并移动到其他文件夹，邮箱服务器上的这些邮件是没有同时被移动的。

3、IMAP协议
    (Internet Mail Access Protocol)协议是对POP3协议的一种扩展，提供webmail 与电子邮件客户端之间的双向通信，
    客户端的操作都会反馈到服务器上，对邮件进行的操作，服务器上的邮件也会做相应的动作。
    IMAP像POP3那样提供了方便的邮件下载服务，让用户能进行离线阅读。

IMAP 整体上为用户带来更为便捷和可靠的体验。POP3 更易丢失邮件或多次下载相同的邮件，但 IMAP 通过邮件客户端与webmail 之间的双向同步功能很好地避免了这些问题。



# 二、模块构成



![image-20220627165923078](/Users/fansm/Library/Application Support/typora-user-images/image-20220627165923078.png)

# 三、遇到的问题及解决过程

## 问题1：qq邮箱登录认证失败


  现象：使用正确的邮箱地址以及邮箱密码，登录qq邮箱，却返回认证失败。
  解决：登录第三方客户端，需要使用授权码进行登录。
    

**qq邮箱授权码如何获取**

1、首先我们进入qq邮箱页面：**[https://mail.qq.com/](https://mail.qq.com/，登录自己的账号；)**，登录自己的账号；

![img](https://www.win7zhijia.cn/upload/20220414/3f59f98236b3011eda0129b23e8fb8b7.png)

2、我们点击上方的“设置”按钮，然后切换到“账户”页面，滑动到底部的“POP3/IMAP...”；

![img](https://www.win7zhijia.cn/upload/20220414/a2ab9be18e2b45dc97f5dbe9346bbfb7.png)

3、然后点击“开启”按钮，进入到确认对话框，根据提示发送手机短信；

4、如果上方的号码发送短信失败，那就点击“我已发送”，然就选择下面的号码就行；

![img](https://www.win7zhijia.cn/upload/20220414/e981958f9dffc9293c98f56c92538ed1.jpg)

5、最后最后进入此界面，中间就是授权码了。

![img](https://www.win7zhijia.cn/upload/20220414/014397926d6047d2b7b0c698bcfa8fec.jpg)



## 问题2：获取新邮件并保存到本地遇到的问题

现象1：使用pop3协议每次都获取全部邮件，重复获取

​		pop3协议无法把邮件标记为已读，每次从服务器获取邮件时，所有的邮件都要重新获取。

2、使用IMAP协议，有的邮件保存本地后再次读取时，发生 BASE64Decoder异常

```
程序报错：
com.sun.mail.util.DecodingException: BASE64Decoder: Error in encoded stream: needed 4 valid base64 characters but only got 1 before EOF, the 10 most recent characters were: "VFT0YK\r\n v"
```

解决思路：

pop3和imap两个协议一起配合使用。

1）、使用pop3协议获取邮件列表（popList）

2）、使用Imap协议获取邮件列表（imapList）

3）、循环处理popList邮件列表中的邮件，通过唯一ID（MessageNumber）从imapList中取出相应的邮件，通过判断邮件标志是否为已读进行后续的处理。

4）、如果是未读，使用popList中的邮件，保存到本地。使用imapList中的邮件，标记为已读。

5）、如果为已读，该邮件不做任何处理，从popList中取下一个邮件重复进行3）的处理

