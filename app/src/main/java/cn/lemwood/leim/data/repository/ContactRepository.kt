package cn.lemwood.leim.data.repository

import androidx.lifecycle.LiveData
import cn.lemwood.leim.data.dao.ContactDao
import cn.lemwood.leim.data.model.Contact
import cn.lemwood.leim.data.model.ContactType

class ContactRepository(private val contactDao: ContactDao) {
    
    fun getContactsByType(type: ContactType): LiveData<List<Contact>> = 
        contactDao.getContactsByType(type)
    
    fun getAllContacts(): LiveData<List<Contact>> = contactDao.getAllContacts()
    
    suspend fun getContactById(contactId: String): Contact? = contactDao.getContactById(contactId)
    
    suspend fun getContactByLeimId(leimId: String): Contact? = contactDao.getContactByLeimId(leimId)
    
    suspend fun insertContact(contact: Contact) = contactDao.insertContact(contact)
    
    suspend fun insertContacts(contacts: List<Contact>) = contactDao.insertContacts(contacts)
    
    suspend fun updateContact(contact: Contact) = contactDao.updateContact(contact)
    
    suspend fun deleteContact(contact: Contact) = contactDao.deleteContact(contact)
    
    suspend fun deleteContactById(contactId: String) = contactDao.deleteContactById(contactId)
    
    suspend fun updateUnreadCount(contactId: String, count: Int) = 
        contactDao.updateUnreadCount(contactId, count)
    
    suspend fun updateLastMessage(contactId: String, message: String, time: Long) = 
        contactDao.updateLastMessage(contactId, message, time)
    
    suspend fun searchContacts(query: String): List<Contact> = contactDao.searchContacts(query)
}