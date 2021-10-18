# Telegram group notify bot

Telegram bot for notify custom users in telegram group chat

## Use

Host your own instance

Bot reads all messages in group and use public instance is unsecure

### Quick guide:

* add bot to group chat
* add user to custom group (custom group create automatically. All groups start from `@`)
* link group name into any messages in group chat and bot link all group members

### Commands:

`/add groupName member - add member to group. Support multiply members - Example: /add @all @Member`

`/remove groupName member - remove member from group. Support multiply members - Example: /remove @all @Member`

`/groups - list all custom groups in group chat - Example: /groups`

`/group groupName - list all users in custom group - Example: /group @all`

### Installation

#### introductions

To run your own instance, you must follow the following Telegram Bot
Requirements: https://core.telegram.org/bots/webhooks#the-short-version

Current version doesn't support self-sign certs for webhook registration

#### build and run

* clone repository
* Fill telegram bot params in `src/main/resources/application.yml`
* Run `.\gradlew bootJar`
* Copy artifact from `build\libs\group-notify-telegram-bot-0.0.1.jar` to your server
* Run artifact on the server with Java 11+ `java -jar group-notify-telegram-bot-0.0.1.jar`

You can pack it into docker container



