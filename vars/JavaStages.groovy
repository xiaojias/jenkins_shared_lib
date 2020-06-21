// Run stage of 'clone'
def Clone(String stageName){
    println "Stage info: $stageName"
}

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