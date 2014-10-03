import os
import sys
import shutil
from java.io import FileInputStream
from java.util import Properties


# Utils methods

def loadProperties(fileName):
    props = Properties()
    input = FileInputStream(fileName)
    props.load(input)
    input.close()

    print props

    result= {}
    for entry in props.entrySet():
        result[entry.key] = entry.value

    return result

def startTransaction():
    edit()
    startEdit()

def endTransaction():
    save()
    activate(block="true")

def getJmsServerName(prefix):
    return prefix + 'JMSServer'

def getFileStoreName(prefix):
    return prefix + 'JMSFileStore'

def getModuleNameByPrefix(prefix):
    return prefix + 'JMSModule'

def getSubDeploymentNameByPrefix(prefix):
    return prefix + 'JMSSubDeployment'

def getJMSModulePath(jmsModuleName):
    return "/JMSSystemResources/" + jmsModuleName + "/JMSResource/" + jmsModuleName

def getJMSModulePathByPrefix(prefix):
    return getJMSModulePath(getModuleNameByPrefix(prefix))

def getUDQueuePathByName(udqName):
    return '/UniformDistributedQueues/' + udqName

# JMS methods
def deleteWorkManager(domainName, workManager):
    print "========================================="
    print "Deleting " + workManager + "..."
    print "========================================="
    stMBean=getMBean("/SelfTuning/" + domainName)
    wmMBean=getMBean("/SelfTuning/" + domainName + "/WorkManagers/" + workManager)
    if wmMBean != None:
        stMBean.destroyWorkManager(wmMBean)
        print "destroyed " + workManager
		
def deleteMaxThreadsConstraints(domain, maxThreadsConstraints):
    print "========================================="
    print "Deleting " + maxThreadsConstraints + "..."
    print "========================================="
    stMBean=getMBean("/SelfTuning/" + domain)
    mtcMBean=getMBean("/SelfTuning/" + domain + "/MaxThreadsConstraints/" + maxThreadsConstraints)
    if mtcMBean != None:
        stMBean.destroyMaxThreadsConstraint(mtcMBean)
        print "destroyed max threads constraint " + maxThreadsConstraints
		
def deleteResponseTimeRequestClass(domain, responseTimeRequestClass):
    print "========================================="
    print "Deleting " + responseTimeRequestClass + "..."
    print "========================================="
    stMBean=getMBean("/SelfTuning/" + domain)
    rtrMBean=getMBean("/SelfTuning/" + domain + "/ResponseTimeRequestClasses/" + responseTimeRequestClass)
    if rtrMBean != None:
        stMBean.destroyResponseTimeRequestClass(rtrMBean)
        print "destroyed " + responseTimeRequestClass
		
def setMaxThreadsConstraintNone(domain, workManager):
    print "========================================="
    print "Updating " + workManager + "..."
    print "========================================="
    defaultWMInstance = getMBean("/SelfTuning/" + domain + "/WorkManagers/" + workManager)
    if defaultWMInstance is not None:
        defaultWMInstance.setMaxThreadsConstraint(None)	
			
def deleteJmsModule(jmsModule):
    print "========================================="
    print "Deleting " + jmsModule + "..."
    print "========================================="
    if getMBean("/JMSSystemResources/" + jmsModule) != None:
        jmsSystemResource = delete(jmsModule, "JMSSystemResource")
        print "\n"
		
def deleteDataSource(dataSourse):	
    print "========================================="
    print "Deleting " + dataSourse + "..."
    print "========================================="	
    if getMBean("/JDBCSystemResources/" + dataSourse) != None:
        jmsSystemResource = delete(dataSourse, "JDBCSystemResources")
        print "\n"
		
def deleteJmsServer(jmsServer):
    print "========================================="
    print "Deleting " + jmsServer + "..."
    print "========================================="	
    if getMBean("/JMSServers/" + jmsServer) != None:
        jmsSystemResource = delete(jmsServer, "JMSServer")
		
def deleteFileStore(fileStore):
    print "========================================="
    print "Deleting " + fileStore + "..."
    print "========================================="	
    if getMBean("/FileStores/" + fileStore) != None:
        jmsSystemResource = delete(fileStore, "FileStores")
		
def deleteCF(jmsServer, connectionFactory):
    print "========================================="
    print "Deleting " + connectionFactory + "..."
    print "========================================="	
    cfMBean=getMBean("/JMSSystemResources/" + jmsServer + "/JMSResource/" + jmsServer + "/ConnectionFactories/")
    cfInstanceMBean=getMBean("/JMSSystemResources/" +jmsServer + "/JMSResource/" + jmsServer + "/ConnectionFactories/" + connectionFactory)
    if cfInstanceMBean != None:
        cfMBean.destroyConnectionFactory(cfInstanceMBean) 
		
def deleteQueue(jmsServer, queue):
    print "========================================="
    print "Deleting " + queue + "..."
    print "========================================="	
    queueMBean=getMBean("/JMSSystemResources/"+jmsServer+"/JMSResource/"+jmsServer+"/UniformDistributedQueues/")
    qInstanceMBean=getMBean("/JMSSystemResources/"+jmsServer+"/JMSResource/"+jmsServer+"/UniformDistributedQueues/"+queue)
    if qInstanceMBean != None:
        queueMBean.destroyUniformDistributedQueue(qInstanceMBean) 

# Main code

print "***** START *****"
properties = loadProperties('local.properties')
try:
    # Init vars
    providerUrl = properties['PROVIDERURL']
    username = properties['USERNAME']
    password = properties['PASSWORD']
    
    osbServer = properties['OSB_SERVER_NAME']
    osbServerFileStorePath = properties['OSB_SERVER_JMS_FILESTORE_PATH']

    # Start configuration cluster

    connect(username, password, providerUrl)
    domainName = cmo.name
	
	#OSB
    startTransaction()
    deleteWorkManager(domainName, "SingleThreadWorkManager")
    deleteWorkManager(domainName, "TestPaymentWorkManager")
    deleteJmsModule("OSBJMSModule")
    deleteJmsServer("OSBJMSServer")
    endTransaction()

except:
    dumpStack()
    cancelEdit("y")
    raise

print "\ndisconnecting..."
disconnect()
print "***** COMPLETE *****"