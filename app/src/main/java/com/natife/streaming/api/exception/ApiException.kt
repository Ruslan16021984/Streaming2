package com.natife.streaming.api.exception

import java.io.IOException
import java.lang.Exception

class ApiException(message: String,val body:String?) : IOException(message)  {
}