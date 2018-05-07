package agartha.data.services

import agartha.data.db.conn.MongoConnection
import agartha.data.objects.IntentionDBO
import agartha.data.objects.SettingsDBO
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.replaceOne
import org.litote.kmongo.toList

/**
 * Purpose of this file is reading settings from data storage
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-04-12.
 */
class SettingsService : ISettingsService {

    // Get MongoDatabase
    private val database = MongoConnection.getDatabase()
    // MongoCollection
    protected val collection = database.getCollection<SettingsDBO>(CollectionNames.SETTINGS_SERVICE.collectionName)

    /**
     * The settings storage can only have one item
     * @param item Default settings object to insert (if collection is empty)
     * @return Stored settings data object
     */
    override fun insert(item: SettingsDBO): SettingsDBO {
        val items = getAll()
        if (items.isNotEmpty()) {
            return items.first()
        }
        return item.apply {
            collection.insertOne(item)
        }
    }

    /**
     * Update the SettingsDBO with a new intention to the intentions-list
     */
    override fun addIntention(item: IntentionDBO): SettingsDBO {
        // Get the settingsDBO
        val settingsObject: SettingsDBO = getAll()[0]
        // Create a copy of the intentions and add the new intention to the copy
        val copyIntentionMutableList: MutableList<IntentionDBO> = settingsObject.intentions.toMutableList()
        copyIntentionMutableList.add(item)
        // Create a updatedSettingsDBO with all the same variables as the old one except the new intentionsList
        val updatedSettingsDBO = SettingsDBO(settingsObject._id, copyIntentionMutableList, settingsObject.disciplines, settingsObject.companionDays, settingsObject.companionGoalHours)
        // Update the SettingsSBO
        settingsObject.apply {
            collection.replaceOne(updatedSettingsDBO)
        }
        return updatedSettingsDBO
    }

    override fun getById(id: String): SettingsDBO? {
        return collection.findOneById(id)
    }

    override fun getAll(): List<SettingsDBO> {
        return collection.find().toList()
    }


}