//package com.lightningkite.kotlin.crossplatform.view.console
//
//import lk.kotlin.crossplatform.view.ViewFactory
//import org.junit.Test
//import kotlin.reflect.full.declaredFunctions
//
//class VirtualGenerator{
//    @Test fun generateVirtual(){
//        //Let us reflect upon the view factory.
//
//        for(function in ViewFactory::class.declaredFunctions){
//            val className = function.name.capitalize() + "View"
//            val typeParameters = function.typeParameters.let{
//                if(it.isEmpty()) ""
//                else it.joinToString(", ", "<", ">"){ it.name }
//            }
//            val classMembers = function.parameters.asSequence().drop(1).joinToString { "var ${it.name}: ${it.type}" }
//            val functionParameters = function.parameters.asSequence().drop(1).joinToString { "${it.name}: ${it.type}" }
//            val functionParametersPassing = function.parameters.asSequence().drop(1).joinToString { it.name ?: "null" }
//            val newClass = """class $className$typeParameters($classMembers): View()"""
//            val newFunction = """override fun $typeParameters ${function.name}($functionParameters):$className$typeParameters = $className($functionParametersPassing)"""
//            println(newClass.replace("VIEW", "View"))
//            println(newFunction.replace("VIEW", "View"))
//        }
//    }
//}