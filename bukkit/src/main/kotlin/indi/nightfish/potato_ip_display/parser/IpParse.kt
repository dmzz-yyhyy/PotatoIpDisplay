package indi.nightfish.potato_ip_display.parser

import indi.nightfish.potato_ip_display.util.IpData

interface IpParse {
    fun getRegion(): String
    fun getCountry(): String
    fun getProvince(): String
    fun getCity(): String
    fun getISP(): String
    fun getFallback(): String

    fun toIpData(): IpData = IpData(
        region = getRegion(),
        country = getCountry(),
        province = getProvince(),
        city = getCity(),
        isp = getISP(),
        fallback = getFallback()
    )
}