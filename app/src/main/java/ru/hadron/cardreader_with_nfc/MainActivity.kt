package ru.hadron.cardreader_with_nfc

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NfcAdapter.getDefaultAdapter(this)
    }

    /**
     *  Start sending APDU commands once a card is detected
     */
    override fun onTagDiscovered(tag: Tag?) {
        val isoDep = IsoDep.get(tag)
        isoDep.connect()

        val response = isoDep.transceive(Utils.hexStringToByteArray("00A4040007A0000002471001"))

        runOnUiThread {
            tv_card_response.append(("\nCard Response: "
                    + Utils.toHex(response)))
        }

        isoDep.close()
    }

    /**
     * This will enable reader mode while this activity is running. When dealing with a Smart Card,
     * make sure you search the technology it is using, so you declare it, but mostly
     * you can use NFC_A. The second flag is there so we skip the NDEF interfaces,
     * what that means in our case, is we don’t want Android Beam to be called when on this activity,
     * otherwise, it will interfere with our reader, because Android gives priority
     * to NDEF type before TECH type (or Smart Cards in General)
     */
    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableReaderMode(
            this,
            this,
            NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
        null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }
}