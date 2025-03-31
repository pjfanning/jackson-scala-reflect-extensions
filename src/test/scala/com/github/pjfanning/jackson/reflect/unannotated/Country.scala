package com.github.pjfanning.jackson.reflect.unannotated

class Country(val name: String)

class CountryWithCapital(name: String, val capital: String) extends Country(name)
