package com.fromjin.htss.Model

import java.util.Date

class NewsInfo(val ticker:String,
               val provider: String,
               val date: Date,
               val title:String,
               val link: String)