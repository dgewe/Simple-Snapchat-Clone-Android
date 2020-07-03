package com.fredrikbogg.snapchatclone.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fredrikbogg.snapchatclone.R
import com.fredrikbogg.snapchatclone.adapters.UserSnapsListAdapter
import com.fredrikbogg.snapchatclone.models.Snap
import com.fredrikbogg.snapchatclone.utils.FirebaseAuthHelper
import com.fredrikbogg.snapchatclone.utils.FirebaseDatabaseHelper
import com.fredrikbogg.snapchatclone.utils.ProgressDialogHandler

class ShowRetrievedSnapsActivity : AppCompatActivity(),
    FirebaseDatabaseHelper.OnRetrieveUserSnapsListener {
    private val progressDialogHandler: ProgressDialogHandler = ProgressDialogHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_retrieved_snaps)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loadSnaps()
    }

    private fun loadSnaps() {
        progressDialogHandler.toggleProgressDialog(this, true)
        val uid = FirebaseAuthHelper.getCurrentUser()?.uid

        if (uid != null) {
            FirebaseDatabaseHelper.getSnapsFromUser(this, uid)
        }
    }

    private fun setupSnapsListView(snapsList: ArrayList<Snap>?) {
        val snapsListView: ListView = findViewById(R.id.listView)
        val adapter = UserSnapsListAdapter(
            this,
            R.layout.list_item_snap_from_user, snapsList
        )
        snapsListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        snapsListView.adapter = adapter

        snapsListView.setOnItemClickListener { _, _, position, _ ->
            snapsList?.get(position)?.let {
                snapsList.removeAt(position)
                adapter.notifyDataSetChanged()
                showSnap(it)
            }
        }
    }

    private fun showSnap(snap: Snap) {
        Intent(this, ShowSnapActivity::class.java).let {
            it.putExtra(ShowSnapActivity.EXTRA_DB_IMAGE_ID, snap.id)
            it.putExtra(ShowSnapActivity.EXTRA_DB_IMAGE_STORAGE_PATH_ID, snap.path)
            startActivity(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onUserSnapsRetrieved(success: Boolean, data: ArrayList<Snap>?) {
        progressDialogHandler.toggleProgressDialog(this, false)

        if (success) {
            if (data != null && data.size > 0) {
                setupSnapsListView(data)
            } else {
                Toast.makeText(this, "No snaps", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "Error loading snaps", Toast.LENGTH_SHORT).show()
        }
    }
}