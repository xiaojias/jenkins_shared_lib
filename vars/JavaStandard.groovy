// ==========================================================================
// Jenkinsfile for Template of JAVA standard, template is named as: java_std
// ==========================================================================

def P           // Properties got from YAMLs
def APP_NAME    // Application name got from YAML, e.g ei

node("linux-x86_64"){
            stage('Load configuration'){
                println "Load parameters from configuration files !!!"

                    def template_yaml_repo = 'ssh://git@github.com:xiaojias/devops-cicd.git'
                    def pipeline_yaml_branch = 'pipeline_std'
                    def template_yaml_rootdir = 'pipeline_std/templates/example'

                    println "${pipeline_yaml_branch}"
                    deleteDir()	
                    checkout([$class: 'GitSCM', 
                            branches: [[name: "${pipeline_yaml_branch}"]],   // Regarding environment
                            userRemoteConfigs: [[url: "${pipeline_yaml_repo}"]]]
                            )
                    logOutput = sh returnStdout: true, script: 'git log -n 1'
                    println logOutput

                    def pipeline_yaml = 'merged_yamls_updated.yaml'

                    archiveArtifacts("${pipeline_yaml}")

                    P = readYaml(file : "${pipeline_yaml}")

            }

                properties([
                    buildDiscarder(
                        logRotator(artifactDaysToKeepStr: '30', artifactNumToKeepStr: '100', daysToKeepStr: '30', numToKeepStr: '100')),
                    parameters([
                        choice(choices: "2.5", description:'Involved Template Version', name: 'template_version'),
                        string(defaultValue: "${P.input.params.git_url.configured_value}", description: "Git URL", name: 'git_url'),
                        string(defaultValue: "${P.input.params.branch_name.configured_value}", description:"Branch/TAG Name or Commit ID", name: 'branch_name'),
                        string(defaultValue: "linux-x86_64", description:'Building Node/Label', name: 'building_node'),
                        string(defaultValue: "${env.JOB_NAME}", description:" ", name: 'parent_jobname'),
                        [$class: 'WHideParameterDefinition', defaultValue: "${env.JOB_NAME}.yaml".toLowerCase(), description: 'Specifical Properties file', name: 'spec_profile']
                        ])
                ])            
}            
node("${params.building_node}"){
            def new_workingdir = "${env.WORKSPACE}/${params.parent_jobname}"

        ws("${new_workingdir}"){
            timestamps {
                deleteDir()
                // For clone stage
                def m_data = []

                StageClone(P.stages.clone, m_data)

            }
        }
}


// Run stage of 'clone'
def StageClone(S_CLONE, m1){
    println "Stage data: ${S_CLONE}"
    stage("${S_CLONE.description}"){
        // For clone stage
        if (! "${S_CLONE.skip}".toBoolean()){
            println "Running stage: clone - ${S_CLONE.description}"
            println "Stage info: ${S_CLONE}"

            // Involve clone_code (step name is: clone_code)
            step_name = 'clone_code'
            step_skip = "${S_CLONE.steps.clone_code.skip}"

            if (! "${step_skip}".toBoolean()){
                // println "Running step: ${step_name} - ${S_CLONE.steps.clone_code.description}"
                // println "Step info: ${S_CLONE.steps.clone_code}"

                deleteDir()	
                // println "Jenkins worker os is unix: ${isUnix()}"

                checkout([$class: 'GitSCM', 
                        branches: [[name: "${params.branch_name}"]], 
                        userRemoteConfigs: [[url: "${params.git_url}"]]]
                        )
                if (isUnix()){
                    logOutput = sh returnStdout: true, script: 'git log -n 1'
                } else {
                    logOutput = bat returnStdout: true, script: 'git log -n 1'
                }
                sCommit.processOut(this, logOutput)

                commitData = sCommit.getAll()
                commitHash = commitData.CommitId
                issueLinks = sCommit.getIssueLinks()
                branchName = "${params.branch_name}"

                m1.add(commitData)
                m1.add(commitHash)
                m1.add(issueLinks)

            } else {
                println "Skipped the step."
            }

            // Involve update_submodules (step name is: update_submodules)
            step_name = 'update_submodules'
            step_skip = "${S_CLONE.steps.update_submodules.skip}"

            if (! "${step_skip}".toBoolean()){
                // println "Running step: ${step_name} - ${S_CLONE.steps.update_submodules.description}"
                // println "Step info: ${S_CLONE.steps.update_submodules}"

                for (int i = 0; i < "${S_CLONE.steps.update_submodules.dirs.size()}".toInteger(); i++){
                            if (isUnix()){
                                dir("${S_CLONE.steps.update_submodules.dirs[i]}"){
                                    logOutput = sh returnStdout: true, script:  """
                                        git submodule update --init --recursive
                                        git submodule update --remote --recursive
                                    """
                                }
                            } else {
                                dir("${S_CLONE.steps.update_submodules.dirs[i]}"){
                                    logOutput = bat returnStdout: true, script:  """
                                        git submodule update --init --recursive
                                        git submodule update --remote --recursive
                                    """
                                }
                            }
                            println logOutput 
                
                }
            } else {
                println "Skipped the step."
            }

        } else {
            println "Stage Skipped."
        }
    }
}


