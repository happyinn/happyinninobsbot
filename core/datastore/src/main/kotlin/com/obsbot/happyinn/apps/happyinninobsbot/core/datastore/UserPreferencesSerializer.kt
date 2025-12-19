package com.obsbot.happyinn.apps.happyinninobsbot.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * [UserPreferences] proto 的 [androidx.datastore.core.Serializer] 序列化器
 */
class UserPreferencesSerializer @Inject constructor() : Serializer<UserPreferences> {
    /**
     * 默认的 UserPreferences 实例
     */
    override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

    /**
     * 从输入流中读取并解析 UserPreferences 数据
     *
     * @param input 输入流
     * @return 解析后的 UserPreferences 实例
     * @throws CorruptionException 当协议缓冲区无效时抛出
     */
    override suspend fun readFrom(input: InputStream): UserPreferences =
        try {
            // readFrom 已经在数据存储后台线程上调用
            UserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    /**
     * 将 UserPreferences 数据写入输出流
     *
     * @param t UserPreferences 实例
     * @param output 输出流
     */
    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        // writeTo 已经在数据存储后台线程上调用
        t.writeTo(output)
    }
}