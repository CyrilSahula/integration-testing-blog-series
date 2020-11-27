package com.sahula.integrationtestingblogseries.service.exeption

import kotlin.reflect.KClass

class NotFoundException(val id: Long, val clazz: KClass<*>) : RuntimeException("The entity [${clazz.simpleName}] with ID [$id] not found")