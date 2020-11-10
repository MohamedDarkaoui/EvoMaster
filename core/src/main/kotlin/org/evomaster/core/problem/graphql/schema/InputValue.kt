package org.evomaster.core.problem.graphql.schema

/**
 * InputValue: fragment  on __InputValue
 */
class InputValue {

     var name: String?=null
     var type: TypeRef?=null
     var defaultValue: String?=null


    override fun toString(): String {
        return "{ name: ${this.name}, type: ${this.type}, defaultValue: ${this.defaultValue }"

    }


}
