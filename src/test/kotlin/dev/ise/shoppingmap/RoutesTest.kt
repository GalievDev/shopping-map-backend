package dev.ise.shoppingmap

import dev.ise.shoppingmap.routing.IndexTest
import dev.ise.shoppingmap.routing.v1.CapsulesRouteTest
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite
import org.junit.platform.suite.api.SuiteDisplayName

@Suite
@SuiteDisplayName("Shopping Map - Routes Test")
@SelectClasses(
    value = [
        IndexTest::class,/*
        ClothesRouteTest::class,
        OutfitsRouteTest::class,*/
        CapsulesRouteTest::class
    ]
)
class RoutesTest