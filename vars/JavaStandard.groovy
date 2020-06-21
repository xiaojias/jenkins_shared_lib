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



