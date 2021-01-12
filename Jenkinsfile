@Library('common-libs@master') _
try {
	def err = null
      properties([			
      // pipelineTriggers([cron('30 14 * * 1-5')]),
       parameters([
              string(defaultValue: "nsbhttplistener", description: 'Bitbucket repo name', name: 'REPO_NAME'),
			        choice( name: 'runVulnerabilityScan', choices: "Disable\nEnable", description: 'Enable or Disable Vulnerability Scan' )
              ])
      ])
      timeout(time: 20, unit: 'MINUTES') {
          def appName="${env.REPO_NAME}".toLowerCase()
          def project=""                 
				  def SOURCE_BRANCH="${env.REPO_NAME}_dev"
				  def DESTINATION_BRANCH="${env.REPO_NAME}_env"
				  def PROJECT_KEY="dpnsbmob"
				  def nexusSnapRepo = "dp-nsb-mobile-maven-snapshots"

          node {
            stage("Initialize") {
              project = "dp-nsb-mobile5g"
            }
          }
          node("maven") {

            stage("Preparation") {
              ciBuildGitCheckout(source: SOURCE_BRANCH, destination: DESTINATION_BRANCH, proj: project, projkey: PROJECT_KEY, repo: env.REPO_NAME)
            }

            stage("Software build") {
              ciBuildMavenDocker(app: appName,proj: project)                
            }

            stage("Unit Test"){
              ciBuildUnitTest()
            }
            
			if (env.runVulnerabilityScan == 'Enable') {
				stage('Vulnerability Analysis'){
				  // Scans vulnerability on maven dependencies...
				  ciBuildVulnerabilityCheck()
				}
			}

            stage ('Quality Analysis'){
              ciBuildQualityCheck()
            }

            stage('Upload Artefacts') {
    			ciBuildArtifactUpload(nexussnaprepo: nexusSnapRepo)
			}

              stage ('Publish'){    
                ciBuildPublish(proj: project, projkey: PROJECT_KEY, repo: env.REPO_NAME)        
            }

            stage("Deploy to Dev") {
              cdDeployOpenShift(app: appName, proj: project)
            }
					}

      }
    } catch (err) {
        echo "in catch block"
        echo "Caught: ${err}"
        currentBuild.result = 'FAILURE'
        throw err
      }finally {
        ciBuildSendMail()
      }
