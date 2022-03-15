package hu.gerviba.borrower.converter

import java.lang.String.join
import javax.persistence.AttributeConverter
import javax.persistence.Converter

const val SPLIT_CHAR = ";"

@Converter
class StringListConverter : AttributeConverter<MutableList<String>, String> {

    override fun convertToDatabaseColumn(stringList: MutableList<String>): String {
        return join(SPLIT_CHAR, stringList)
    }

    override fun convertToEntityAttribute(string: String): MutableList<String> {
        return string.split(SPLIT_CHAR).toMutableList()
    }

}
