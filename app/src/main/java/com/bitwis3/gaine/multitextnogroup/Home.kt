@file:JvmName("HomeStatic")
package com.bitwis3.gaine.multitextnogroup

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.google.ads.consent.*
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import io.supercharge.shimmerlayout.ShimmerLayout
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*
import net.frederico.showtipsview.ShowTipsBuilder
import net.frederico.showtipsview.ShowTipsView
import net.frederico.showtipsview.ShowTipsViewInterface
import org.michaelbel.bottomsheet.BottomSheet
import org.michaelbel.bottomsheet.BottomSheetCallback
import osmandroid.project_basics.Task
import spencerstudios.com.bungeelib.Bungee
import spencerstudios.com.fab_toast.FabToast
import java.net.MalformedURLException
import java.net.URL





class Home : AppCompatActivity() {

    internal lateinit var db: DBRoom;
    private var form: ConsentForm? = null

    var interstitialAd: InterstitialAd? = null
    lateinit var shimmerText1: ShimmerLayout
    lateinit var shimmerText2: ShimmerLayout
    lateinit var shimmerText3: ShimmerLayout
    lateinit var shimmerText5: ShimmerLayout
    lateinit var shimmerText4: ShimmerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        Log.i("JOSHyo", "oncreate")
        MobileAds.initialize(this, "ca-app-pub-6280186717837639~2020381737")


        setFonts()
        val pref = getSharedPreferences("AUTO_PREF", MODE_PRIVATE)
        var showTip = pref.getBoolean("showTip", true);

        if(showTip){
            initTheAnims()
            showTip()
        }else{
            initTheAnims()
            checkForConsent()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.test) {
            Task.RateApp(this, "com.gainwise.transactional")
            return true
        }
        if (id == R.id.buyPro) {
            Task.RateApp(this, "com.bitwis3.gaine.multitextnogroup")
            return true
        }
        if (id == R.id.test2) {
            startActivity(Intent(this, Credits::class.java))
            Bungee.card(this)
            return true
        }
        if (id == R.id.share_app) {
            Task.ShareApp(this, "com.bitwis3.gaine.multitextnogroup",
                    "Multi-Text",
                    "A great tool for useful texting features!")

            return true
        }

        if (id == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            Bungee.card(this)
            return true
        }
        if (id == R.id.other_apps) {
            Task.MoreApps(this, "GainWise")
            return true
        }
        if (id == R.id.rate_app) {
            Task.RateApp(this, "com.bitwis3.gaine.multitextnogroup")
            return true
        }


        return super.onOptionsItemSelected(item)
    }


    private fun checkForConsent() {
        val consentInformation = ConsentInformation.getInstance(this@Home)
//
//        ConsentInformation.getInstance(this@Home).addTestDevice("E8F9908FCA6E01002BCD08F42B00E801")
//        ConsentInformation.getInstance(this@Home).debugGeography = DebugGeography.DEBUG_GEOGRAPHY_EEA
        val publisherIds = arrayOf("pub-6280186717837639")
        consentInformation.requestConsentInfoUpdate(publisherIds, object : ConsentInfoUpdateListener {
            override fun onConsentInfoUpdated(consentStatus: ConsentStatus) {
                // User's consent status successfully updated.
                when (consentStatus) {
                    ConsentStatus.PERSONALIZED -> {
                        Log.d("JOSHad", "Showing Personalized ads")
                        showPersonalizedAds()
                    }
                    ConsentStatus.NON_PERSONALIZED -> {
                        Log.d("JOSHad", "Showing Non-Personalized ads")
                        showNonPersonalizedAds()
                    }
                    ConsentStatus.UNKNOWN -> {
                        Log.d("JOSHad", "Requesting Consent")
                        if (ConsentInformation.getInstance(baseContext)
                                        .isRequestLocationInEeaOrUnknown) {
                            Log.d("JOSHad", "helper2")

                            requestConsent()

                        } else {
                            Log.d("JOSHad", "helper3")
                            showPersonalizedAds()
                        }
                    }
                    else -> {
                    }
                }
            }

            override fun onFailedToUpdateConsentInfo(errorDescription: String) {
                // User's consent status failed to update.
            }
        })
    }

    private fun requestConsent() {
        Log.d("JOSHad", "helper1")

        var privacyUrl: URL? = null
        try {
            // TODO: Replace with your app's privacy policy URL.
            privacyUrl = URL("https://docs.google.com/document/d/e/2PACX-1vS0f1URBeRQ6Lrhi1W5KxC6eDjxB46OwZOLv8VKoE6DmN5kpESA7EqHNB0qbt08amyr5Iv-Yx_HXubK/pub")
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            // Handle error.
        }

        form = ConsentForm.Builder(this@Home, privacyUrl)
                .withListener(object : ConsentFormListener() {
                    override fun onConsentFormLoaded() {
                        // Consent form loaded successfully.
                        Log.d("JOSHad", "Requesting Consent: onConsentFormLoaded")
                        showForm()
                    }

                    override fun onConsentFormOpened() {
                        // Consent form was displayed.
                        Log.d("JOSHad", "Requesting Consent: onConsentFormOpened")
                    }

                    override fun onConsentFormClosed(

                            consentStatus: ConsentStatus?, userPrefersAdFree: Boolean?) {
                        startAnims()
                        Log.d("JOSHad", "Requesting Consent: onConsentFormClosed")
                        if (userPrefersAdFree!!) {
                            // Buy or Subscribe
                            Log.d("JOSHad", "Requesting Consent: User prefers AdFree")
                        } else {
                            Log.d("JOSHad", "Requesting Consent: Requesting consent again")
                            when (consentStatus) {
                                ConsentStatus.PERSONALIZED -> showPersonalizedAds()
                                ConsentStatus.NON_PERSONALIZED -> showNonPersonalizedAds()
                                ConsentStatus.UNKNOWN -> showNonPersonalizedAds()
                            }

                        }
                        // Consent form was closed.
                    }

                    override fun onConsentFormError(errorDescription: String?) {
                        Log.d("JOSHad", "Requesting Consent: onConsentFormError. Error - " + errorDescription!!)
                        // Consent form error.
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .withAdFreeOption()
                .build()
        form?.load()
    }

    private fun showPersonalizedAds() {

        interstitialAd = InterstitialAd(this@Home)
        interstitialAd?.setAdUnitId("ca-app-pub-6280186717837639/5716084168")

        interstitialAd?.loadAd(AdRequest.Builder().build())
        interstitialAd?.setAdListener(object : AdListener() {
            override fun onAdClosed() {

                interstitialAd?.loadAd(AdRequest.Builder().build())
            }
        })

    }

    private fun showNonPersonalizedAds() {

        interstitialAd = InterstitialAd(this@Home)
        interstitialAd?.setAdUnitId("ca-app-pub-6280186717837639/5716084168")

        interstitialAd?.loadAd(AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, getNonPersonalizedAdsBundle()).build())
        interstitialAd?.setAdListener(object : AdListener() {
            override fun onAdClosed() {

                interstitialAd?.loadAd(AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, getNonPersonalizedAdsBundle()).build())
            }
        })


    }

    fun getNonPersonalizedAdsBundle(): Bundle {
        val extras = Bundle()
        extras.putString("npa", "1")

        return extras
    }

    private fun showForm() {
        if (form == null) {
            Log.d("JOSHad", "Consent form is null")
        }
        if (form != null) {
            Log.d("JOSHad", "Showing consent form")
            form?.show()
        } else {
            Log.d("JOSHad", "Not Showing consent form")
        }
    }


    override fun onResume() {
        super.onResume()
       startAnims()
        if(infoOn()){
            showInfos()
        }else{
            hideInfos()
        }


        val prefs = getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE)
        var firstRun = prefs.getBoolean("showAd", false)

        if (firstRun) {
            loadAd()
            val editor = prefs.edit()
            editor.putBoolean("showAd", false)
            editor.apply()


        }

    }



    fun loadAd() {
        try {

            if (interstitialAd != null && interstitialAd!!.isLoaded()) {
                interstitialAd?.show()
                Log.d("JOSH", "ad tried to show")
            } else {
                Log.d("JOSH", "ad not loaded")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        Log.d("JOSH", "caught")
    }

    fun infoClicked(v: View){
        when(v.id){
            R.id.card1info ->{initNewBuilder(resources.getString(R.string.card1info))}
            R.id.card2info ->{initNewBuilder(resources.getString(R.string.card2info))}
            R.id.card3info ->{initNewBuilder(resources.getString(R.string.card3info))}
            R.id.card4info ->{initNewBuilder(resources.getString(R.string.card4info))}
            R.id.card5info ->{initNewBuilder(resources.getString(R.string.card5info))}
        }
    }

    fun initNewBuilder(message: String) {
        val builder: BottomSheet.Builder
        builder = BottomSheet.Builder(Home@this)


        builder.setWindowDimming(80)
                .setTitleMultiline(true)
                .setBackgroundColor(Color.parseColor("#313131"))
                .setTitleTextColor(Color.parseColor("#ffffff"))


        builder.setOnShowListener { }
        builder.setOnDismissListener { }

        builder.setCallback(object : BottomSheetCallback {
            override fun onShown() {}

            override fun onDismissed() {}
        })

        builder.setTitle(message)
        builder.show()
    }

    fun infoOn(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
       return sharedPreferences.getBoolean("info", true)
    }

    fun showInfos(){
       card1info.visibility = View.VISIBLE
        card2info.visibility = View.VISIBLE
        card3info.visibility = View.VISIBLE
        card4info.visibility = View.VISIBLE
        card5info.visibility = View.VISIBLE
    }
    fun hideInfos(){
        card1info.visibility = View.GONE
        card2info.visibility = View.GONE
        card3info.visibility = View.GONE
        card4info.visibility = View.GONE
        card5info.visibility = View.GONE
    }

fun goToGroupManage(v: View){
    val intent = Intent(this@Home, CreateContactList::class.java)
    startActivity(intent)
    Bungee.slideLeft(this)
}

    fun goToLog(v: View){
        val intent = Intent(this@Home, ActivityLog::class.java)
        startActivity(intent)
        Bungee.slideLeft(this)
    }

    fun goToMainAct(v: View){
        val intent = Intent(this@Home, MainActivity::class.java)
        startActivity(intent)
        Bungee.slideLeft(this)
    }


  fun  setFonts(){
      val font = Typeface.createFromAsset(this.getAssets(), "Acme-Regular.ttf")

    }

fun initTheAnims(){
    shimmerText2 = findViewById(R.id.shimmer_text_card2) as ShimmerLayout
     shimmerText5 = findViewById(R.id.shimmer_text_card5) as ShimmerLayout
    shimmerText1 = findViewById(R.id.shimmer_text_card1) as ShimmerLayout
    shimmerText3 = findViewById(R.id.shimmer_text_card3) as ShimmerLayout
    shimmerText4 = findViewById(R.id.shimmer_text_card4) as ShimmerLayout


    }

    fun startAnims(){
        var handler = Handler()
        handler.postDelayed({stopAnims()},5000)
        shimmerText2.startShimmerAnimation()
        shimmerText5.startShimmerAnimation()
        shimmerText1.startShimmerAnimation()
        shimmerText3.startShimmerAnimation()
        shimmerText4.startShimmerAnimation()
    }

    fun stopAnims(){
        shimmerText2.stopShimmerAnimation()
        shimmerText5.stopShimmerAnimation()
        shimmerText1.stopShimmerAnimation()
        shimmerText3.stopShimmerAnimation()
        shimmerText4.stopShimmerAnimation()
    }


fun goToTimedText(v: View){
    val intent = Intent(this@Home, MainActivity::class.java)
    intent.putExtra("timed", "timed")
    startActivity(intent)
    Bungee.slideLeft(this)
}

    fun goToAuto(v: View){
        val intent = Intent(this@Home, AutoAct::class.java)
        startActivity(intent)
        Bungee.slideLeft(this)
    }

    fun showTip() {
        var showtips: ShowTipsView = ShowTipsBuilder(Home@this)
                .setTitle("Feature Explanation")
                .setDescription("Click these little icons to see an overview of what each tab does, These can be hidden in the settings once you are familiar with this app.")
                .setDelay(1500)
                .setTarget(card2info)
                .build()

        showtips.show(Home@this)
        showtips.setCallback(object : ShowTipsViewInterface {
            override fun gotItClicked() {
                val editor = getSharedPreferences("AUTO_PREF", MODE_PRIVATE).edit()
                editor.putBoolean("showTip", false)
                editor.apply()
                initTheAnims()
                checkForConsent()
                initTheAnims()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("JOSHd", "onDestroy")
    }

    internal var double_backpressed = false
    override fun onBackPressed() {

        var prefs = getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE)
        var show = prefs.getBoolean("showRateDialog", true)

        if(show){

        var dialog = Dialog(Home@this)
        var view = layoutInflater.inflate(R.layout.rate_buy_exit, null)
        var llbuy = view.findViewById<LinearLayout>(R.id.llbuy);
        var llrate = view.findViewById<LinearLayout>(R.id.llrate);
        var llnever = view.findViewById<LinearLayout>(R.id.llnever);
        var lllater = view.findViewById<LinearLayout>(R.id.lllater);

        llbuy.setOnClickListener({Task.RateApp(Home@this, "com.bitwis3.gaine.multitextnogroup")})
        llrate.setOnClickListener({Task.RateApp(Home@this, "com.bitwis3.gaine.multitextnogroup")
            val editor = getSharedPreferences("AUTO_PREF", MODE_PRIVATE).edit()
            editor.putBoolean("showRateDialog", false)
            editor.apply()})
        llnever.setOnClickListener({
            val editor = getSharedPreferences("AUTO_PREF", MODE_PRIVATE).edit()
            editor.putBoolean("showRateDialog", false)
            editor.apply()
            dialog.dismiss()
            super.onBackPressed()
        })
        lllater.setOnClickListener({
       dialog.dismiss()
            super.onBackPressed()

        })
        dialog.setContentView(view)
        dialog.show()

        }else {


        if (double_backpressed) {
            super.onBackPressed()
            return
        }
        this.double_backpressed = true
        FabToast.makeText(Home@this,
                "Click back again to exit.", Toast.LENGTH_SHORT, FabToast.INFORMATION, FabToast.POSITION_DEFAULT).show()

        Handler().postDelayed({ double_backpressed = false }, 2000)
        }
    }

}

