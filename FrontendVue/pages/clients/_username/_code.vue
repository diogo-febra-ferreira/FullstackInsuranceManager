<template>
    <div>
        <nav class="navbar navbar-expand-md navbar-dark bg-dark">
            <div class="container">
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                    data-bs-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false"
                    aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="navbarNavDropdown">
                    <ul class="navbar-nav mx-auto">
                        <li class="nav-item">
                            <a class="nav-link mx-2 active" aria-current="page"><nuxt-link
                                    :to="`/clients/${this.$auth.user.username}`">Profile</nuxt-link></a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link mx-2 active" aria-current="page"><nuxt-link
                                    :to="`registerOccurrence`">Register
                                    Occurrence</nuxt-link></a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link mx-2 active" aria-current="page"><nuxt-link :to="`myOccurrences`">See my
                                    Occurrences</nuxt-link></a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link mx-2 active" aria-current="page"><nuxt-link :to="`myPolicies`">See my
                                    Policies</nuxt-link></a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link mx-2 active" aria-current="page"><nuxt-link
                                    :to="`registerPolicy`">Register
                                    Policy</nuxt-link></a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link mx-2 btn btn-danger" @click.prevent="signOut">Sign Out</a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
        <div class="mx-auto" style="width: 600px; margin-top: 50px;">
            <b-container class="h-100" style=" text-align: center;">
                <b-row class="h-100">
                    <b-col md="8" class="d-flex flex-column align-items-center">
                        <h4>Occurrence Details: </h4>
                        <div style="margin-top: 30px">
                            <p><b>Description: </b>{{ occurrence.description }}</p>
                            <p><b>Client name: </b>{{ client.name }}</p>
                            <p><b>Client email: </b>{{ client.email }}</p>
                            <p><b>Status: </b>{{ occurrence.status }}</p>
                            <p><b>Insurance: </b>{{ occurrence.insurance }}</p>
                            <p><b> Document Associated: </b>{{ document.filename }}</p>
                        </div>
                        <div class="mx-auto" style="margin-top:50px;">
                            <b-btn class="btn btn-primary" @click.prevent="download(document)"
                                tar-get="_blank">Download</b-btn>
                        </div>
                        <br><br>
                        <h6 v-if="occurrence.status === 'Valid' || occurrence.status === 'Repairing'">


                            <h4>Repairmen working on the occurence: {{ this.repairmenEnrolled.length }}</h4>
                            <b-table v-if="this.repairmenEnrolled.length" striped over :items="repairmenEnrolled"
                                :fields="repairmenEnrolledFields">
                                <template v-slot:cell(actions)="row">
                                </template>
                            </b-table>
                            <p v-else>No Repairmen working on the occurence.</p>

                            <br>
                            <h4>Available repairmen: {{ this.repairmenUnrolled.length }}</h4>
                            <b-table v-if="this.repairmenUnrolled.length" striped over :items="repairmenUnrolled"
                                :fields="repairmenUnrolledFields">
                                <template v-slot:cell(actions)="row">
                                    <button @click.prevent="enroll(row)" class="btn btn-primary">Assign</button>
                                </template>
                            </b-table>
                            <p v-else>No Repairmen available.</p>

                        </h6>
                        <h5 v-else>
                            <p>You cannot assign repairmen, This occurrence is not Valid or Repairing, it is
                                "{{ occurrence.status }}".</p>
                        </h5>

                    </b-col>
                </b-row>
            </b-container>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            occurrence: {},
            client: {},
            repairmen: [],
            document: {},
            repairmenUnrolledFields: [ 'email', 'name', 'actions'],
            repairmenEnrolledFields: [ 'email', 'name'],
            repairmenEnrolled: [],
            repairmenUnrolled: []
        }

    },
    created() {
        //get this occurrence
        this.$axios.$get('/api/occurrences/' + this.code)
            .then((occurrence) => {
                this.occurrence = occurrence || {}
                if (this.occurrence)
                    this.client = occurrence.client
                this.repairmen = occurrence.repairmen
                this.document = occurrence.document
                //get repairmen related to this occurrence
            })

        this.$axios.$get('/api/repairmen/occurrence/' + this.code + '/enrolled')
            .then(repairmenEnrolled => this.repairmenEnrolled = repairmenEnrolled)

        this.$axios.get('/api/repairmen/occurrence/' + this.code + '/unrolled')
            .then((repairmenUnrolled) => {
                this.repairmenUnrolled = repairmenUnrolled.data
                console.log("Debug")
                console.log(this.repairmenUnrolled)
                console.log(this.repairmenUnrolled.data)
            })




    },
    methods: {
        signOut() {
            this.$auth.logout()
            this.$router.push('/')
        },
        download(doc) {

            this.$axios.$get('/api/documents/download/' + doc.id, { responseType: 'arraybuffer' })
                .then(file => {
                    const url = window.URL.createObjectURL(new Blob([file]))
                    const link = document.createElement('a')
                    link.href = url
                    link.setAttribute('download', doc.filename)
                    document.body.appendChild(link)
                    link.click()
                })
        },
        enroll(row) {
            this.$axios.$post('/api/repairmen/' + row.item.username + '/' + this.occurrence.code + '/associate')
                .then(() => {
                    this.$router.go(0)
                }).catch(() => {
                    console.log("Error enrolling repairman")
                })
        },
        unroll(row) {
            this.$axios.$post('/api/repairmen/' + row.item.username + '/' + this.occurrence.code + '/desassociate')
                .then(() => {
                    this.$router.go(0)
                }).catch(() => {
                    console.log("Error desassociating repairman")
                })
        }
    },
    computed: {

        code() {
            return this.$route.params.code
        },
        username() {
            return this.$route.params.username
        },
    }
}
</script>

<style scoped>

</style>
