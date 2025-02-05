package com.unipi.george.chordshub.screens

import android.widget.Toast
import com.unipi.george.chordshub.components.SongLine
import com.unipi.george.chordshub.repository.FirestoreRepository


fun getSongData(repository: FirestoreRepository, callback: (SongLine?, String?) -> Unit) {
    repository.getSongLyrics { lyrics ->
        if (lyrics.isNullOrEmpty()) {
            callback(null, null)
            return@getSongLyrics
        }

        repository.getSongChords { chords ->
            if (chords == null) {
                callback(null, null)
                return@getSongChords
            }

            repository.getSongArtist { artist ->
                val songLine = SongLine(lyrics.joinToString("\n"), chords)
                println("SongLine: $songLine, Artist: $artist")
                callback(songLine, artist)
            }
        }
    }
}


fun saveCardContentAsPdf(context: android.content.Context, title: String, lyrics: List<SongLine>) {
    val pdfDocument = android.graphics.pdf.PdfDocument()

    val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)

    val canvas = page.canvas
    val paint = android.graphics.Paint()

    // Σχεδίαση Τίτλου
    paint.textSize = 20f
    paint.isFakeBoldText = true
    canvas.drawText(title, 50f, 50f, paint)

    // Σχεδίαση Στίχων με Συγχορδίες
    paint.textSize = 16f
    paint.isFakeBoldText = false
    var yPosition = 100f
    lyrics.forEach { line ->
        canvas.drawText(line.lyrics, 50f, yPosition, paint)
        yPosition += 20f
        line.chords.forEach { chord ->
            canvas.drawText(" - ${chord.chord} at ${chord.position}", 70f, yPosition, paint)
            yPosition += 20f
        }
        yPosition += 10f
    }

    pdfDocument.finishPage(page)

    // Αποθήκευση στο φάκελο Downloads
    val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
    val file = java.io.File(downloadsDir, "$title.pdf")
    try {
        pdfDocument.writeTo(java.io.FileOutputStream(file))


        val uri = android.net.Uri.fromFile(file)
        val intent = android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = uri
        context.sendBroadcast(intent)


        Toast.makeText(context, "PDF saved to Downloads: ${file.absolutePath}", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error saving PDF: ${e.message}", Toast.LENGTH_LONG).show()
    } finally {
        pdfDocument.close()
    }
}

