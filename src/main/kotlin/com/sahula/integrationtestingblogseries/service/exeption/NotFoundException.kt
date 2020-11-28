package com.sahula.integrationtestingblogseries.service.exeption

import kotlin.reflect.KClass

class NotFoundException(id: Long, clazz: KClass<*>) : RuntimeException("The entity [${clazz.simpleName}] with ID [$id] not found")