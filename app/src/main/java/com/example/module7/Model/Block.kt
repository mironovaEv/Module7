package com.example.module7.Model


class Block(val type: String, val comparison: String) {
    var name: String? = null
    private var value: String? = null
    fun getNameEditText(): String? {
        return name
    }

    fun getValueEditText(): String? {
        return value
    }

    fun setNameEditText(editTextValue: String) {
        this.name = editTextValue
    }

    fun setValueEditText(editTextValue: String) {
        this.value = editTextValue
    }
}
