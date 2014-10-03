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

def createWorkManager(wmName, domainName, serverName, maxThreadCount=None, minThreadCount=None, responseTimeGoalInSeconds=None, ignoreStuckThreads=None):
    print "========================================="
    print "Creating WorkManager " + wmName + "..."
    print "========================================="	
    
    domainPath = "/SelfTuning/" + domainName
    cd(domainPath)
    wmmbean = getMBean(domainPath)
	
    cd("/Servers/%s" % serverName)
    servermb = cmo	
    if getMBean(domainPath + "/WorkManagers/" + wmName) is None:
        workManagerInstance = wmmbean.createWorkManager(wmName)
        workManagerInstance.addTarget(servermb)
        print "WorkManager '" + wmName + "' created successfully."
    else:
        workManagerInstance = getMBean(domainPath + "/WorkManagers/" + wmName)
        print "WorkManager '" + wmName + "' already exist."

    if maxThreadCount is not None:
        maxThreadsName = wmName + "MaxThreads"
        maxThreadsInstance = getMBean(domainPath + "/MaxThreadsConstraints/" + maxThreadsName)
        if maxThreadsInstance is None:
            maxThreadsInstance = wmmbean.createMaxThreadsConstraint(maxThreadsName)
            maxThreadsInstance.addTarget(servermb)
            maxThreadsInstance.setCount(maxThreadCount)
            workManagerInstance.setMaxThreadsConstraint(maxThreadsInstance)
            print "MaxThreadsConstraint '" + maxThreadsName + "' created successfully."
        else:
            maxThreadsInstance.setCount(maxThreadCount)
            print "MaxThreadsConstraint '" + maxThreadsName + "' already exist."

    if minThreadCount is not None:
        minThreadsName = wmName + "MinThreads"
        minThreadsInstance = getMBean(domainPath + "/MinThreadsConstraints/" + minThreadsName)
        if minThreadsInstance is None:
            minThreadsInstance = wmmbean.createMinThreadsConstraint(minThreadsName)
            minThreadsInstance.addTarget(servermb)
            minThreadsInstance.setCount(minThreadCount)
            workManagerInstance.setMinThreadsConstraint(minThreadsInstance)
            print "MinThreadsConstraint '" + minThreadsName + "' created successfully."
        else:
            maxThreadsInstance.setCount(maxThreadCount)
            print "MinThreadsConstraint '" + minThreadsName + "' already exist."

    if responseTimeGoalInSeconds is not None:
        responseTimeName = wmName + "ResponseTime"
        request = getMBean(domainPath + "/ResponseTimeRequestClasses/" + responseTimeName)
        if request is None:
            request = wmmbean.createResponseTimeRequestClass(responseTimeName)
            request.addTarget(servermb)
            request.setGoalMs(responseTimeGoalInSeconds * 1000) # seconds * milliseconds
            workManagerInstance.setResponseTimeRequestClass(request)
            print "ResponseTimeRequestClass '" + responseTimeName + "' created successfully."
        else:
            request.setGoalMs(responseTimeGoalInSeconds * 1000) # seconds * milliseconds
            print "ResponseTimeRequestClass '" + responseTimeName + "' already exist."

    if ignoreStuckThreads is not None:
        workManagerInstance.setIgnoreStuckThreads(ignoreStuckThreads)

    print "\n"


def createIgnoreStuckThreadsWorkManager(domainName, serverName):
    wmName = "IgnoreStuckThreadsWorkManager"
    print "========================================="
    print "Creating WorkManager " + wmName + " with IgnoreStuckThreads..."
    print "========================================="

    cd('/Servers/' + serverName)
    targetServer = cmo

    domainPath = "/SelfTuning/" + domainName
    cd(domainPath)

    wmmbean = getMBean(domainPath)
    if getMBean(domainPath + "/WorkManagers/" + wmName) is None:
        workManagerInstance = wmmbean.createWorkManager(wmName)
        workManagerInstance.addTarget(targetServer)
        workManagerInstance.setIgnoreStuckThreads(true)
        print "WorkManager '" + wmName + "' created successfully."
    else:
        print "WorkManager '" + wmName + "' already exist."
    print "\n"


def createDataSource(dsName, jndiName, url, driverName, user, password, serverName):
    print "========================================="
    print "Creating Data Source " + dsName + "..."
    print "========================================="

    cd('/Servers/' + serverName)
    targetServer = cmo

    jdbcSystemResource = getMBean("/JDBCSystemResources/" + dsName)
    if jdbcSystemResource is None:
        cd('/')
        jdbcSystemResource = create(dsName, "JDBCSystemResource")
        jdbcSystemResource.addTarget(targetServer)
        jdbcResource = jdbcSystemResource.getJDBCResource()
        jdbcResource.setName(dsName)
        jdbcResource.JDBCDataSourceParams.setJNDINames(jarray.array([String(jndiName)], String))
        jdbcResource.JDBCDriverParams.setUrl(url)
        jdbcResource.JDBCDriverParams.setDriverName(driverName)
        property = jdbcResource.JDBCDriverParams.getProperties().createProperty("USER")
        property.setValue(user)
        jdbcResource.JDBCDriverParams.setPassword(password)
        jdbcResource.JDBCConnectionPoolParams.setTestTableName("SQL SELECT 1 FROM aq$_q_table_s")
    else:
        print "Data Source '" + dsName + "' already exist."
    print "\n"


def createForeignJMSServer(prefix, fsName, dataSourceJndi, cfLocalJndi, cfRemoteJndi, destLocalJndi, destRemoteJndi):
    print "========================================="
    print "Creating Foreign JMS Server " + fsName + "..."
    print "========================================="
    foreignServer = getMBean(getJMSModulePathByPrefix(prefix) + "/ForeignServers/" + fsName)
    if foreignServer is None:
        jmsResource = getMBean(getJMSModulePathByPrefix(prefix))
        # Create & Target foreignServer
        foreignServer = jmsResource.createForeignServer(fsName)
        foreignServer.setInitialContextFactory("oracle.jms.AQjmsInitialContextFactory")
        foreignServer.setDefaultTargetingEnabled(true)
        jndiProperty = foreignServer.createJNDIProperty("datasource")
        jndiProperty.setValue(dataSourceJndi)
        # Create foreignServer Connection Factory
        foreignJMSConnectionFactory = foreignServer.createForeignConnectionFactory("AqCF")
        foreignJMSConnectionFactory.setLocalJNDIName(cfLocalJndi)
        foreignJMSConnectionFactory.setRemoteJNDIName(cfRemoteJndi)
        # Create foreignServer Destination
        foreignServerDest = foreignServer.createForeignDestination("AqDest")
        foreignServerDest.setLocalJNDIName(destLocalJndi)
        foreignServerDest.setRemoteJNDIName(destRemoteJndi)
        print "Foreign JMS Server '" + fsName + "' created successfully."
    else:
        print "Foreign JMS Server '" + fsName + "' already exist."
    print "\n"


def createRedeliveryUDQueue(prefix, udqName, udqErrorName, jndiPrefix, redeliveryLimit, redeliveryDelayInSeconds, unitOfOrderValue = None, messagingPerformancePreference = None):
    print "========================================="
    print "Creating Uniform Distributed Queue " + udqName + "..."
    print "========================================="

    jmsModulePath = getJMSModulePathByPrefix(prefix)
    udqNamePath = getUDQueuePathByName(udqName)
    udqNameFullPath = jmsModulePath + udqNamePath

    udQueue = getMBean(udqNameFullPath)
    if udQueue is None:
        cd(jmsModulePath)
        cmo.createUniformDistributedQueue(udqName)
        udQueue = getMBean(udqNameFullPath)
        udQueue.setJNDIName(jndiPrefix + udqName)
        udQueue.setSubDeploymentName(getSubDeploymentNameByPrefix(prefix))
        udQueue.setLoadBalancingPolicy('Round-Robin')

        udQueue.getMessageLoggingParams().setMessageLoggingEnabled(true)
        udQueue.getMessageLoggingParams().setMessageLoggingFormat('JMSDestination,JMSMessageID,JMSTimestamp')
        
        udQueue.getDeliveryFailureParams().setRedeliveryLimit(redeliveryLimit)
        udQueue.getDeliveryParamsOverrides().setDeliveryMode('Persistent')
        udQueue.getDeliveryParamsOverrides().setRedeliveryDelay(redeliveryDelayInSeconds * 1000) # seconds * milliseconds
        if unitOfOrderValue is not None:
            udQueue.setDefaultUnitOfOrder(unitOfOrderValue)
            print "setDefaultUnitOfOrder = 1"
			
        if messagingPerformancePreference is not None:
            udQueue.setMessagingPerformancePreference(messagingPerformancePreference)
            print "setMessagingPerformancePreference = 1"
        
        if udqErrorName is not None:
            udqErrorNamePath = getJMSModulePathByPrefix(prefix) + getUDQueuePathByName(udqErrorName)
            udErrorQueue = getMBean(udqErrorNamePath)
            udQueue.getDeliveryFailureParams().setErrorDestination(udErrorQueue)
            udQueue.getDeliveryFailureParams().setExpirationPolicy('Redirect')
        
        print "Uniform Distributed Queue '" + udqName + "' created successfully."
    else:
        if udQueue.getDeliveryParamsOverrides().getDeliveryMode() != 'Persistent':
            udQueue.getDeliveryParamsOverrides().setDeliveryMode('Persistent')
            print "setDeliveryMode=Persistent"
        print "Uniform Distributed Queue '" + udqName + "' already exist."
    print "\n"


def createUDQueue(prefix, udqName, udqErrorName, jndiPrefix, unitOfOrderValue = None, messagingPerformancePreference = None):
    createRedeliveryUDQueue(prefix, udqName, udqErrorName, jndiPrefix, 2, 5, unitOfOrderValue, messagingPerformancePreference)


def createErrorUDQueue(prefix, udqName, jndiPrefix):
    print "========================================="
    print "Creating Error Uniform Distributed Queue " + udqName + "..."
    print "========================================="

    jmsModulePath = getJMSModulePathByPrefix(prefix)
    udqNamePath = getUDQueuePathByName(udqName)
    udqNameFullPath = jmsModulePath + udqNamePath
    errorQueue = getMBean(udqNameFullPath)
    if errorQueue is None:
        cd(jmsModulePath)
        cmo.createUniformDistributedQueue(udqName)
        cd(udqNameFullPath)
        cmo.setJNDIName(jndiPrefix + udqName)
        cmo.setSubDeploymentName(getSubDeploymentNameByPrefix(prefix))
        cmo.getDeliveryParamsOverrides().setDeliveryMode('Persistent')
        print "Uniform Error Distributed Queue '" + udqName + "' created successfully."
    else:
        if errorQueue.getDeliveryParamsOverrides().getDeliveryMode() != 'Persistent':
            errorQueue.getDeliveryParamsOverrides().setDeliveryMode('Persistent')
            print "setDeliveryMode=Persistent"
        print "Uniform Error Distributed Queue '" + udqName + "' already exist."
    print "\n"

def createRedeliveryUDQWithErrorQueue(prefix, udqName, udqErrorName, jndiPrefix, redeliveryLimit, redeliveryDelayInSeconds, unitOfOrderValue = None):
    createErrorUDQueue(prefix, udqErrorName, jndiPrefix)
    createRedeliveryUDQueue(prefix, udqName, udqErrorName, jndiPrefix, redeliveryLimit, redeliveryDelayInSeconds, unitOfOrderValue = None)
    print "\n"


def createUDQWithErrorQueue(prefix, udqName, udqErrorName, jndiPrefix, unitOfOrderValue = None, messagingPerformancePreference = None):
    createErrorUDQueue(prefix, udqErrorName, jndiPrefix)
    createUDQueue(prefix, udqName, udqErrorName, jndiPrefix, unitOfOrderValue, messagingPerformancePreference)
    print "\n"



def createCF(prefix, cfName, jndiPrefix, xaEnabled, transTimeout=None):
    print "========================================="
    print "Creating Connection Factory " + cfName + "..."
    print "========================================="   
    jmsModulePath = getJMSModulePathByPrefix(prefix)
    cfPath = jmsModulePath + '/ConnectionFactories/'+ cfName
    if getMBean(cfPath) is None:
        cd(jmsModulePath)
        cf = create(cfName, "ConnectionFactory")
        cf.setJNDIName(jndiPrefix + cfName)
        cf.setSubDeploymentName(getSubDeploymentNameByPrefix(prefix))
        cf.transactionParams.setXAConnectionFactoryEnabled(xaEnabled)
        if transTimeout is not None:
            cf.transactionParams.setTransactionTimeout(transTimeout)
    else:
        print "Connection Factory '" + cfName + "' already exist."
    print "\n"

def createSubDeploymentOnServer(prefix, targetServers):
    moduleName = getModuleNameByPrefix(prefix)
    subDeploymentName = getSubDeploymentNameByPrefix(prefix)
    subDeploymentPath = '/SystemResources/' + moduleName + '/SubDeployments/' + subDeploymentName
    if getMBean(subDeploymentPath) is None:
        cd('/JMSSystemResources/' + moduleName)
        jmsSubDeployment = create(subDeploymentName,'SubDeployment')
        for serverName in targetServers:
            target = getMBean("/JMSServers/" + serverName)
            jmsSubDeployment.addTarget(target)
        print "SubDeployment '" + subDeploymentName + "' created successfully."
    else:
        print "SubDeployment '" + subDeploymentName + "' already exist."
    print "\n"

def createJMSModuleOnServer(prefix, serverName):
    print "========================================="
    print "Creating JMSModule '" + prefix + "' on server '" + serverName + "'..."
    print "========================================="    
    cd('/Servers/' + serverName)
    servermb = cmo

    moduleName = getModuleNameByPrefix(prefix)
    if getMBean("/JMSSystemResources/" + moduleName) is None:
        cd('/')
        jmsModule = create(moduleName, "JMSSystemResource")
        jmsModule.addTarget(servermb)
    else:
        print "JMSModule '" + moduleName + "' already exist."
    print "\n"

def createJMSServer(prefix, wlServer, fileStorePath, stuckThreadTime=None):
    jmsServerName = getJmsServerName(prefix)
    print "========================================="
    print "Creating JMSServer '" + jmsServerName + "' on server '" + wlServer + "'..."
    print "========================================="

    cd("/Servers/" + wlServer)
    targetServer = cmo

    if stuckThreadTime is not None:
        cmo.setStuckThreadMaxTime(stuckThreadTime)

    if getMBean("/JMSServers/" + jmsServerName) is None:
        # Create Filestore
        fileStoreName = getFileStoreName(prefix + "_Test")
        cd('/')
        filestore = cmo.createFileStore(fileStoreName)
        cd('/FileStores/' + fileStoreName)
        filestore.setDirectory(fileStorePath)
        filestore.addTarget(targetServer)
        # Create JMS server and assign the Filestore
        cd("/JMSServers")
        jmsServer = create(jmsServerName, "JMSServer")
        jmsServer.setPersistentStore(filestore)
        jmsServer.addTarget(targetServer)
    else:
        print "JMSServer '" + jmsServerName + "' already exist."
    print "\n"

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
    jndiPrefix = "jms.tutorial."
    osbPrefix = "OSB"

    startTransaction()
    createJMSServer(osbPrefix, osbServer, osbServerFileStorePath, 2*60*60) # 2 hours in seconds
    endTransaction()

    startTransaction()
    createJMSModuleOnServer(osbPrefix, osbServer)

    osbServers=[]
    osbServers.append(getJmsServerName(osbPrefix))
    createSubDeploymentOnServer(osbPrefix, osbServers)
	
    createCF(osbPrefix, "TutorialXACF", jndiPrefix, true)
    createUDQWithErrorQueue(osbPrefix, "TestQueue", "TestErrorQueue", jndiPrefix)
    createUDQWithErrorQueue(osbPrefix, "TestRegistryQueue", "TestRegistryErrorQueue", jndiPrefix)
    createUDQWithErrorQueue(osbPrefix, "SourceQueue", "SourceErrorQueue", jndiPrefix)
    createUDQWithErrorQueue(osbPrefix, "DestinationQueue", "DestinationErrorQueue", jndiPrefix)

    createWorkManager("SingleThreadWorkManager", domainName, osbServer, 1, None, None, false) 
    createWorkManager("TestPaymentWorkManager", domainName, osbServer, 5, None, None, false)
    
    endTransaction()
	
except:
    dumpStack()
    cancelEdit("y")
    raise

print "\ndisconnecting..."
disconnect()
print "***** COMPLETE *****"