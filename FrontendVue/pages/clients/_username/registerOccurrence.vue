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
                            <a class="nav-link mx-2 active" aria-current="page">
                                <nuxt-link :to="`/clients/${this.$auth.user.username}`">Profile
                                </nuxt-link>
                            </a>
                        </li>
                        <li class="nav-item">

                            <a class="nav-link mx-2 active" aria-current="page">
                                <nuxt-link :to="`registerOccurrence`">Register
                                    Occurrence
                                </nuxt-link>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link mx-2 active" aria-current="page">
                                <nuxt-link :to="`myOccurrences`">See my
                                    Occurrences
                                </nuxt-link>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link mx-2 active" aria-current="page">
                                <nuxt-link :to="`myPolicies`">See my
                                    Policies
                                </nuxt-link>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link mx-2 active" aria-current="page">
                                <nuxt-link :to="`registerPolicy`">Register
                                    Policy
                                </nuxt-link>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link mx-2 btn btn-danger" @click.prevent="signOut">Sign Out</a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
        <div class="mx-auto" style="width: 600px; margin-top: 50px;">
            <div class="mt-3" style="text-align: center;">
                <a><b>Register an Occurrence</b></a>
            </div>
            <div class="form-group">
                <label for="Occurrence: "></label>
                <textarea class="form-control" id="exampleFormControlTextarea1" rows="3"
                    placeholder="Describe your occurrence" :state="hasDescription" v-model="description"></textarea>
            </div>
            <div class="form-group">
                <label for="Occurrence: "></label>
                <textarea class="form-control" id="exampleFormControlTextarea1" rows="1" placeholder="Type: "
                    :state="hasType" v-model="type"></textarea>
            </div>
            <div class="form-group">
                <div class="form-group">
                    <label for="exampleFormControlInput1">Insurance</label>
                    <b-form-select v-model="policy" :options="this.options"></b-form-select>
                </div>
            </div>
            <div>
                <form @submit.prevent="upload">
                    <b-form-file v-model="file" :state="hasFile" placeholder="Choose a file or drop it here..."
                        drop-placeholder="Drop file here..." />

                    <div class="mt-3">
                        Selected file: {{ file? file.name : '' }}
                    </div>
                    <div class="mt-3" style="text-align: center; padding: 50px;">
                        <nuxt-link class="btn btn-link" :to="`/clients/${username}`">
                            Back
                        </nuxt-link>
                        <b-button type="submit" :disabled="!hasFile">Register</b-button>
                    </div>
                </form>
            </div>

        </div>
    </div>
</template>

<script>

export default {
    data() {
        return {
            description: null,
            type: null,
            file: null,
            occurrence: {
                client: {},
            },
            policies: [],
            policy: null,
            options: [
                { value: null, text: 'Please select an option', disabled: true }
            ],
        }
    },
    computed: {
        username() {
            return this.$route.params.username
        },

        hasFile() {
            return this.file != null
        },
        hasDescription() {
            return this.description != null
        },
        hasType() {
            return this.type != null
        },
        formData() {
            let formData = new FormData()
            formData.append('username', this.$auth.user.username)
            if (this.file) {
                formData.append('file', this.file)
            }
            if (this.description) {
                formData.append('description', this.description)
            }
            if (this.type) {
                formData.append('type', this.type)
            }
            if (this.policy != null) {
                const insurance = this.policies.find(pol => pol.code == this.policy)
                //console.log(insurance)
                formData.append('insurance', insurance.insurance)
            }
            return formData
        }
    },
    created() {
        this.$axios.$get(`api/policies/client/${this.username}`)
            .then(policies => {
                this.policies = JSON.parse(JSON.stringify(policies));
                if (this.policies.length === 0) {
                    this.$toast.error('Please register your insurance policies in the app').goAway(3000)
                } else {
                   // console.log(this.policies)
                    for (const pol of policies) {
                        const option = { value: pol.code, text: pol.insurance + ' - Policy Number: ' + pol.code }
                        this.options.push(option)
                    }
                }
            })
        this.$axios.$get(`api/clients/${this.username}`)
            .then((client) => {
                this.occurrence.client = client;
            })
            .catch(() => {
                console.log("Error retrieving client")
            })
    },

    methods: {
        signOut() {
            this.$auth.logout()
            this.$router.push('/')
        },
        upload() {
            if (!this.hasFile) {
                this.$toast.error('Please select a file to upload.').goAway(3000)
                return
            }
            if (!this.hasDescription) {
                this.$toast.error('Please describe your occurrence.').goAway(3000)
                return
            }
            if (!this.hasType) {
                this.$toast.error('Please specify the type of occurrence.').goAway(3000)
                return
            }

            if (this.policies.length === 0) {
                this.$toast.error('Please register your insurance policies in the app').goAway(3000)
                return;
            }

            if (this.policy == null) {
                this.$toast.error("Please select an insurance policy").goAway(3000);
                return;
            }

            this.$axios.$post('/api/documents', this.formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            })
                .then((document) => {
                    let insurance = ''
                    if (this.policy != null) {
                        insurance = this.policies.find(pol => pol.code == this.policy)

                    }
                    this.$axios.$post('api/occurrences/create/' + this.policy, {
                        "description": this.description,
                        "insurance": insurance.insurance,
                        "client": { "username": this.occurrence.client.username, "email": this.occurrence.client.email, "name": this.occurrence.client.name },
                        "document": document.at(0),
                        "type": this.type
                    })
                        .then(() => {
                            this.$toast.success("Success registering occurrence").goAway(3000);
                            this.$router.push(`/clients/${this.username}/myOccurrences`)
                        })
                        .catch(() => {
                            this.$toast.error('Error registering occurrence').goAway(3000);
                        })
                })
                .catch(() => {
                    this.$toast
                        .error("Sorry, error uploading document. Couldn't register occurrence")
                        .goAway(3000)
                })
        }
    }
}
</script>
