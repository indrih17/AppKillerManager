package com.thelittlefireman.appkillermanager.utils

enum class Manufacturer(private val device: String) {
    Xaiomi("xiaomi"),
    Samsung("samsung"),
    Oppo("oppo"),
    Huawei("huawei"),
    Meizu("meizu"),
    OnePlus("oneplus"),
    Letv("letv"),
    Asus("asus"),
    Htc("htc"),
    Zte("zte"),
    Vivo("vivo");

    override fun toString(): String = device
}
