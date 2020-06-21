#!groovy

// Run stage of 'clone'
def stageClone(S_CLONE, m1){
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
                // sCommit.processOut(this, logOutput)

                // commitData = sCommit.getAll()
                // commitHash = commitData.CommitId
                // issueLinks = sCommit.getIssueLinks()
                // branchName = "${params.branch_name}"

                // m1.add(commitData)
                // m1.add(commitHash)
                // m1.add(issueLinks)

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

// For config_artifactory stage
def stageConfigArtifactory(S_ARTICACTORY){
    println "Stage data: ${S_ARTICACTORY}"
    stage("${S_ARTICACTORY.description}"){
        // For config_artifactory stage
        if (! "${S_ARTICACTORY.skip}".toBoolean()){
            println "Running stage: ${S_ARTICACTORY.name} - ${S_ARTICACTORY.description}"
            println "Stage info: ${S_ARTICACTORY}"
            println "TBD Actions !!!"
        } else {
            println "Stage Skipped."
        }
    }
}

// For get_version_number stage
def stageGetVersionNumber(S_GETVER, appName){
    println "Stage data: ${S_GETVER}"
    stage("${S_GETVER.description}"){
        // For get_version_number stage
        if (! "${S_GETVER.skip}".toBoolean()){
            println "Running stage: ${S_GETVER.name} - ${S_GETVER.description}"
            println "Stage info: ${S_GETVER}"
            println "TBD Actions !!!"
        } else {
            println "Stage Skipped."
        }
    }
}

// For build stage
def stageBuild(S_BUILD){
    println "Stage data: ${S_BUILD}"
    stage("${S_BUILD.description}"){
        // For get_version_number stage
        if (! "${S_BUILD.skip}".toBoolean()){
            println "Running stage: ${S_BUILD.name} - ${S_BUILD.description}"
            println "Stage info: ${S_BUILD}"
            println "TBD Actions !!!"
        } else {
            println "Stage Skipped."
        }
    }
}

// For 'test' stage
def stageTest(S_TEST){
    // Parameters: P.stages.test
    stage("${S_TEST.description}"){
        // For test
        if (! "${S_TEST.skip}".toBoolean()){
            println "Running stage: ${S_TEST.name} - ${S_TEST.description}"
            println "Stage info: ${S_TEST}"
            // Involve unit_test (step name is: unit_test)
            step_name = 'unit_test'
            step_skip = false
            // Check if step_run depeneds on params.skip_test & steps.unit_test.skip
            if ("${params.skip_test}" != "null"){
                step_skip = "${params.skip_test}".toBoolean()
            } else {
                step_skip = "${S_TEST.steps.unit_test.skip}".toBoolean()
            }
            
            if (! "${step_skip}".toBoolean()){
                println "Running step: ${step_name} - ${S_TEST.steps.unit_test.description}"
                println "Step info: ${S_TEST.steps.unit_test}"
                // Run for junit
                actions_count = "${S_TEST.steps.unit_test.actions.size()}"
                for (int runsid = 0; runsid < "${actions_count}".toInteger(); runsid++){
                    if ( "${S_TEST.steps.unit_test.actions[runsid].execute_type}" == "junit" ){
                        // Run for junit
                        junit "${S_TEST.steps.unit_test.actions[runsid].test_file}"
                    }
                }                    

            } else {
                println "Skipped the step."
            }

        } else {
            println "Stage Skipped."
        }
    }
}

// // Example
// // For get_version_number stage
// def stageGetVersionNumber(S_GETVER){
//     println "Stage data: ${S_GETVER}"
//     stage("${S_GETVER.description}"){
//         // For get_version_number stage
//         if (! "${S_GETVER.skip}".toBoolean()){
//             println "Running stage: ${S_GETVER.name} - ${S_GETVER.description}"
//             println "Stage info: ${S_GETVER}"
//             println "TBD Actions !!!"
//             } else {
//                 println "Skipped the step."
//             }
//         } else {
//             println "Stage Skipped."
//         }
//     }
// }
