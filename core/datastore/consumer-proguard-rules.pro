# 保留 DataStore 字段
# 此规则用于保护所有继承自 GeneratedMessageLite 的类的字段
# 确保 protobuf 消息序列化的兼容性
#
# 说明：保留 protobuf 生成的消息类的所有字段成员
# 防止代码混淆器在代码优化期间删除这些字段
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite* {
   <fields>;
}
