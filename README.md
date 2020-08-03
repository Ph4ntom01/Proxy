# **Proxy**

[![Discord Bots](https://top.gg/api/widget/status/592680274962415635.svg)](https://top.gg/bot/592680274962415635)
[![Discord Bots](https://top.gg/api/widget/lib/592680274962415635.svg)](https://top.gg/bot/592680274962415635)
[![Discord Bots](https://top.gg/api/widget/owner/592680274962415635.svg)](https://top.gg/bot/592680274962415635)

## **Commands**

***For more information about a specific command, use !help [command]***

|Categories|Commands|
|-|-|
|Administration|`prefix` `setadmin` `setmodo` `setuser` `joinchan` `joinmsg` `joinbox` `leavechan` `leavemsg` `leavebox` `defrole` `shield` `disable`|
|Moderation|`clean` `slowmode` `lock` `unlock` `voicekick` `voicemute` `voiceunmute` `kick` `ban` `softban` `unban` `purge` `resetchan`|
|Utility|`ping` `uptime` `server` `member` `avatar` `textchan` `controlgate` `banlist` `modolist` `adminlist`|
|Memes|`issou`|

## **How to activate the shield**

The bot will kick accounts that have been created less than [any value you want from 0 to 30] days ago.

Example:

`!shield 0` *disables the shield.*

`!shield 3` *kicks an account created less than 3 days.*

## **Top things to do:**

### **1. Create an administrator role named 'bot' assigned to Proxy**

Set this role in the uppermost position:

![bot_role_img](https://raw.githubusercontent.com/Unknown-Ph4ntom/Proxy/master/attachments/bot_role.png)

You can also set Proxy's role directly in the uppermost position as well.

### **2. Set a default role**

`!defrole @aRole`

*This role will be automatically assigned to members who join your server.*

### **3. Set a welcoming and leaving notification channel**

`!joinchan #aTextChannel` `!leavechan #aTextChannel`

*These channels will be used to send welcoming and leaving messages.*

### **4. Set a message**

`!joinmsg yourMessage` `!leavemsg yourMessage`

*Add [member] if you want the bot to mention the member.*

### **5. Activate boxes**

`!joinbox on` `!leavebox on`

## **How to disable a functionnality**

Use the command `!disable` and you will get started.

## **Hosting**

Proxy is currently hosted 24/7 on a VPS and uses MariaDB for its database.