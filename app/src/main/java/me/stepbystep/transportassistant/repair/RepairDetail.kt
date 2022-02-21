package me.stepbystep.transportassistant.repair

enum class RepairDetail(
    val displayName: String,
    val requiredMileage: Int,
) {
    Oil("Машинное масло", 10000),
    OilFilter("Масляный фильтр", 10000),
    AirFilter("Воздушный фильтр", 10000),
    PetrolFilter("Топливный фильтр", 20000),
    GasFilter1("Газовый фильтр 1", 10000),
    GasFilter2("Газовый фильтр 2", 10000),
    BrakePads1("Передние тормозные колодки", 20000),
    BrakePads2("Задние тормозные колодки", 20000),
    BeltReplacement("Замена ремня ГРМ", 60000),
}