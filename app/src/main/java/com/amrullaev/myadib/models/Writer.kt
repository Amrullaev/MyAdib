package com.amrullaev.myadib.models

import java.io.Serializable

class Writer : Serializable {
    var fullname:String?=null
    var bornyear:String?=null
    var deathyear:String?=null
    var typeWriter:String?=null
    var info:String?=null
    var photoUrl:String?=null


    constructor()
    constructor(
        fullname: String?,
        bornyear: String?,
        deathyear: String?,
        typeWriter: String?,
        info: String?,
        photoUrl: String?,
    ) {
        this.fullname = fullname
        this.bornyear = bornyear
        this.deathyear = deathyear
        this.typeWriter = typeWriter
        this.info = info
        this.photoUrl = photoUrl
    }
}