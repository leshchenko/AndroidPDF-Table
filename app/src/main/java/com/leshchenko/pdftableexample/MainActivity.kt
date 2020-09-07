package com.leshchenko.pdftableexample

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.leshchenko.pdftable.*
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val outputStream = File(getExternalFilesDir(null)?.absolutePath + "test.pdf").outputStream()
        val rows = mutableListOf<Row>()
        val columns = mutableListOf<Column>()
        columns.add(
            Column(
                cells = listOf(
                    Cell(
                        data = "The laboratory tests showed the result of the above data in Takasi system showed that the result of the above data was Negative, as the sample was taken on xx/xx/xxxx.\nUpon his request, this scene was given to whomever may concern.\nAccordingly, this report has been written at xx/xx/xxxx.",
                        preferences = Preferences(alignType = AlignTypes.LEFT)
                    )
                )
            )
        )
        columns.add(
            Column(
                cells = listOf(
                    Cell(
                        data = "Centered text",
                        preferences = Preferences(alignType = AlignTypes.CENTER, underLinedText = true)
                    )
                )
            )
        )
        columns.add(
            Column(
                cells = listOf(
                    Cell(
                        data = "Test right aligned text with colored background",
                        preferences = Preferences(
                            alignType = AlignTypes.RIGHT,
                            backgroundColor = Color.GREEN
                        )
                    ), Cell(
                        data = "Test bold with background color",
                        preferences = Preferences(
                            backgroundColor = Color.GREEN,
                            typeface = Typeface.DEFAULT_BOLD
                        )
                    ),
                    Cell(
                        data = "Test with custom margins",
                        preferences = Preferences(
                            textMargin = Margins(24f, 0f, 2f, 4f)
                        )
                    ),
                    Cell(
                        data = "Test without borders and custom text color",
                        preferences = Preferences(
                            drawBorders = false,
                            textColor = Color.MAGENTA
                        )
                    )
                )
            )
        )

        rows.add(Row(columns = columns, preferences = Preferences(Color.DKGRAY)))

        // Adding image
        rows.add(
            Row(
                columns = listOf(
                    Column(
                        cells = listOf(
                            Cell(
                                data = getFileFromAssets(
                                    baseContext,
                                    "test.jpeg"
                                ).absolutePath,
                                dataType = DataTypes.IMAGE
                            )
                        )
                    )
                )
            )
        )

        Table(
            file = outputStream,
            rows = rows,
            preferences = Preferences(backgroundColor = Color.YELLOW)
        ).drawTable()
    }
}

@Throws(IOException::class)
fun getFileFromAssets(context: Context, fileName: String): File = File(context.cacheDir, fileName)
    .also {
        it.outputStream().use { cache -> context.assets.open(fileName).use { it.copyTo(cache) } }
    }